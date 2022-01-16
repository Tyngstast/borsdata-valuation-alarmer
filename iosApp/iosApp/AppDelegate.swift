import shared
import SwiftUI
import BackgroundTasks

class AppDelegate: NSObject, UIApplicationDelegate {
    
    func application(_ application: UIApplication, didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey: Any]?) -> Bool {
        print("registering background task")
        
        BGTaskScheduler.shared.register(forTaskWithIdentifier: "com.github.tyngstast.valuation_processing", using: nil) { task in
            print("trying to call ")
            self.handleValuationProcessing(task: task as! BGProcessingTask)
        }
        return true
    }
    
    func scheduleValuationProcessing() {
        print("scheduleValuationProcessing")
        let request = BGProcessingTaskRequest(identifier: "com.github.tyngstast.valuation_processing")
        request.requiresNetworkConnectivity = true
        request.requiresExternalPower = false
        
        do {
            try BGTaskScheduler.shared.submit(request)
        } catch {
            print("Could not schedule app refresh: \(error)")
        }
    }
    
    func handleValuationProcessing(task: BGProcessingTask) {
        print("handleValuationProcessing")
        scheduleValuationProcessing()
        
        let queue = OperationQueue()
        queue.maxConcurrentOperationCount = 1
        
        let valuationAlarmOperation = ValuartionAlarmOperation()
        
        task.expirationHandler = {
            print("task expired")
            queue.cancelAllOperations()
        }
        
        valuationAlarmOperation.completionBlock = {
            let success = !valuationAlarmOperation.isCancelled
            print("valuationAlarmOperation CompletionBlock")
            task.setTaskCompleted(success: success)
        }
        
        queue.addOperation(valuationAlarmOperation)
        
////        let context = PersistentContainer.shared.newBackgroundContext()
////        let operations = Operations.getOperationsToFetchLatestEntries(using: context, server: server)
//        let lastOperation = operations.last!
//
//        task.expirationHandler = {
//            // After all operations are cancelled, the completion block below is called to set the task to complete.
//            queue.cancelAllOperations()
//        }
//
//        lastOperation.completionBlock = {
//            task.setTaskCompleted(success: !lastOperation.isCancelled)
//        }
//
//        queue.addOperations(operations, waitUntilFinished: false)
    }
}
