import BackgroundTasks
import FirebaseMessaging
import shared

let TOPIC = "triggerValuationAlarmWorker"

class ValuationAlarmWorker {
    private static let log = koin.loggerWithTag(tag: "ValuationAlarmWorker")
    
    static let VALUATION_PROCESSING_TASK = "com.github.tyngstast.valuationProcessing"
    
    static let alarmWorkerModel = Models.shared.getValuationAlarmWorkerModel()
    static let schedulingModel = Models.shared.getSchedulingModel()
    
    static func onFailure() {
        Messaging.messaging().unsubscribe(fromTopic: TOPIC, completion: { error in
            log.d { "Unsubscribed from topic: \(TOPIC)" }
        })
        NotificationFactory.sendErrorNotification()
    }
    
    static func onMessageReceived() {
        log.d { "Message received" }
        
        if schedulingModel.scheduleNext() {
            log.d { "Scheduling next worker execution" }
            let request = BGProcessingTaskRequest(identifier: VALUATION_PROCESSING_TASK)
            request.earliestBeginDate = Date(timeIntervalSinceNow: randomInitialDelay())
            request.requiresExternalPower = false
            request.requiresNetworkConnectivity = true
            do {
                try BGTaskScheduler.shared.submit(request)
            } catch {
                log.e { "Failed to schedule valuation alarm work. \(error)" }
            }
        } else {
            log.e { "Failure threshold reached. Notifying user to open app and re-sync" }
            onFailure()
        }
    }
    
    static func process(task: BGProcessingTask) {
        log.d { "Processing Alarm Sync" }
    
        alarmWorkerModel.run(onFailure: onFailure) { triggerMessages, err in
            guard let actualTriggerMessages = triggerMessages else {
                log.e { "Error during worker run: \(err)" }
                onFailure()
                task.setTaskCompleted(success: false)
                return
            }
        
            actualTriggerMessages.forEach { message in
                NotificationFactory.sendAlarmTriggeredNotification(message: message)
            }
    
            task.setTaskCompleted(success: true)
        }
    }
    
    /// Delay between 30 seconds and 5 minutes to not ddos backend
    static func randomInitialDelay() -> Double {
        Double.random(in: 30..<300)
    }
}
