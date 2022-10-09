import SwiftUI
import FirebaseCore
import FirebaseMessaging
import FirebaseAnalytics
import shared

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
    var alarmWorkerModel: ValuationAlarmWorkerModel?
    func application(_ application: UIApplication,
                     didFinishLaunchingWithOptions _: [UIApplication.LaunchOptionsKey: Any]?) -> Bool {
        FirebaseApp.configure()
        
        startKoin()
        alarmWorkerModel = Models.shared.getValuationAlarmWorkerModel()
        
        UNUserNotificationCenter.current().delegate = self
        let authOptions: UNAuthorizationOptions = [.alert, .badge]
        UNUserNotificationCenter.current().requestAuthorization(options: authOptions) { _, _ in }
        application.registerForRemoteNotifications()
    
        Messaging.messaging().delegate = self
        // TODO: create multiple notifications for job results
        // TODO: scheduleNext() otherwise unsubscribe
        // TODO: shared model for scheduling logic that can be injected
        // iphone 14 emulator deviceId: windows -> simulators
    
        return true
    }
}

extension AppDelegate: UNUserNotificationCenterDelegate {
    // Required when swizzling is disabled
    func application(_ application: UIApplication,
                     didRegisterForRemoteNotificationsWithDeviceToken deviceToken: Data) {
        print("*** didRegisterForRemoteNotificationsWithDeviceToken ***")
        Messaging.messaging().apnsToken = deviceToken
    }
    
    // User taps notification?
    // TODO: Open app or could possibly remove?
    func userNotificationCenter(_ center: UNUserNotificationCenter,
                                didReceive response: UNNotificationResponse,
                                withCompletionHandler completionHandler: @escaping () -> Void) {
        print("*** didReceive ***")
        completionHandler()
    }
    
    // App in foreground
    func userNotificationCenter(_ center: UNUserNotificationCenter,
                                willPresent notification: UNNotification,
                                withCompletionHandler completionHandler: @escaping (UNNotificationPresentationOptions) -> Void) {
        print("*** willPresent ***")
        completionHandler([[.banner]])
    }

    // App in background
    func application(_: UIApplication,
                     didReceiveRemoteNotification userInfo: [AnyHashable: Any]) async -> UIBackgroundFetchResult {
        print("*** didReceiveRemoteNotification ***")
        // Print full message.
        print(userInfo)

        Messaging.messaging().appDidReceiveMessage(userInfo)
    
        // if scheduleNext() -> run

        return UIBackgroundFetchResult.newData
    }
}

extension AppDelegate: MessagingDelegate {
    func messaging(_ messaging: Messaging, didReceiveRegistrationToken fcmToken: String?) {
        let tokenDict = ["token": fcmToken ?? ""]
        NotificationCenter.default.post(
            name: Notification.Name("FCMToken"),
            object: nil,
            userInfo: tokenDict
        )
    }
}