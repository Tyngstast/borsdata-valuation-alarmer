import BackgroundTasks
import FirebaseAnalytics
import FirebaseCore
import FirebaseMessaging
import shared
import SwiftUI

@main
struct IOSApp: App {
    @UIApplicationDelegateAdaptor(AppDelegate.self) var delegate

    var body: some Scene {
        WindowGroup {
            AppRootView {
                ContentView()
            }
        }
    }
}

class AppDelegate: NSObject, UIApplicationDelegate {
    func application(_ application: UIApplication,
                     didFinishLaunchingWithOptions _: [UIApplication.LaunchOptionsKey: Any]?) -> Bool {
        FirebaseApp.configure()

        startKoin()

        UNUserNotificationCenter.current().delegate = self
        application.registerForRemoteNotifications()

        Messaging.messaging().delegate = self
    
        BGTaskScheduler.shared.register(forTaskWithIdentifier: ValuationAlarmWorker.VALUATION_PROCESSING_TASK, using: nil) { task in
            task.expirationHandler = {
                task.setTaskCompleted(success: false)
            }

            ValuationAlarmWorker.process(task: task as! BGProcessingTask)
        }
        
        return true
    }
}

extension AppDelegate: UNUserNotificationCenterDelegate {
    // Required when swizzling is disabled
    func application(_: UIApplication,
                     didRegisterForRemoteNotificationsWithDeviceToken deviceToken: Data) {
        print("*** didRegisterForRemoteNotificationsWithDeviceToken ***")
        Messaging.messaging().apnsToken = deviceToken
    }

    // User taps notification
    func userNotificationCenter(_: UNUserNotificationCenter,
                                didReceive _: UNNotificationResponse,
                                withCompletionHandler completionHandler: @escaping () -> Void) {
        print("*** didReceive ***")
        // TODO: this should be triggered from FCM when app is in both background and foreground.
        ValuationAlarmWorker.onMessageReceived()
    
        completionHandler()
    }

    // App in foreground
    func userNotificationCenter(_: UNUserNotificationCenter,
                                willPresent _: UNNotification,
                                withCompletionHandler completionHandler: @escaping (UNNotificationPresentationOptions) -> Void) {
        print("*** willPresent ***")

        completionHandler([[.banner]])
    }
    
    // App in background ?
    func application(_: UIApplication,
                     didReceiveNotificationResponse userInfo: [AnyHashable: Any],
                     fetchCompletionHandler completionHandler: @escaping (UIBackgroundFetchResult) -> Void) {
        print("*** didReceiveNotificationResponse ***")
        print(userInfo)

        Messaging.messaging().appDidReceiveMessage(userInfo)

        completionHandler(.newData)
    }

    // App in background ?
    func application(_: UIApplication,
                     didReceiveRemoteNotification userInfo: [AnyHashable: Any],
                     fetchCompletionHandler completionHandler: @escaping (UIBackgroundFetchResult) -> Void) {
        print("*** didReceiveRemoteNotification ***")
        print(userInfo)

        Messaging.messaging().appDidReceiveMessage(userInfo)

        completionHandler(.newData)
    }
}

extension AppDelegate: MessagingDelegate {
    func messaging(_: Messaging, didReceiveRegistrationToken fcmToken: String?) {
        print("*** FirebaseMessaging Token: \(fcmToken ?? "")")
        let tokenDict = ["token": fcmToken ?? ""]
        NotificationCenter.default.post(
            name: Notification.Name("FCMToken"),
            object: nil,
            userInfo: tokenDict
        )
    }
}
