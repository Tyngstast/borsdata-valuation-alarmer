import BackgroundTasks
import FirebaseMessaging
import shared

class ValuationAlarmWorker {
    private static let log = koin.loggerWithTag(tag: "ValuationAlarmWorker")
    
    static let VALUATION_PROCESSING_TASK = "com.github.tyngstast.valuationProcessing"
    
    static let separatorString = NSLocalizedString("notification_message_trigger_word", comment: "notification alarm triggered separator word")
    
    static let alarmWorkerModel = Models.shared.getValuationAlarmWorkerModel()
    static let schedulingModel = Models.shared.getSchedulingModel()
    
    static func onFailure() {
        Messaging.messaging().unsubscribe(fromTopic: TOPIC)
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
        
        alarmWorkerModel.run(translatedSeparatorWord: separatorString, onFailure: onFailure) { triggerMessages, err in
            guard let actualTriggerMessages = triggerMessages else {
                NotificationFactory.sendErrorNotification()
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
