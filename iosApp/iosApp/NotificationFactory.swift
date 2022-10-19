import SwiftUI

class NotificationFactory {
    
    private static let triggerNotificationTitle = NSLocalizedString("notification_alarm_triggered_title", comment: "alarm triggered notification title")
    private static let notificationErrorTitle = NSLocalizedString("notification_error_title", comment: "notification error title")
    private static let notificationErrorMessage = NSLocalizedString("notification_error_message", comment: "notification error message")
    
    static func sendAlarmTriggeredNotification(message: String) {
        sendNotification(title: triggerNotificationTitle, message: message)
    }
    
    static func sendErrorNotification() {
        sendNotification(title: notificationErrorTitle, message: notificationErrorMessage)
    }
    
    private static func sendNotification(title: String, message: String) {
        let center = UNUserNotificationCenter.current()
        
        let content = UNMutableNotificationContent()
        content.title = title
        content.body = message
        
        let trigger = UNTimeIntervalNotificationTrigger.init(timeInterval: 1, repeats: false)
        let request = UNNotificationRequest(identifier: UUID().uuidString, content: content, trigger: trigger)
        center.add(request)
    }
}
