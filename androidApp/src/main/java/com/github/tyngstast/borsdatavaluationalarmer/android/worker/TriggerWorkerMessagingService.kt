package com.github.tyngstast.borsdatavaluationalarmer.android.worker

import co.touchlab.kermit.Logger
import com.github.tyngstast.borsdatavaluationalarmer.SharedModel
import com.github.tyngstast.borsdatavaluationalarmer.injectLogger
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.firebase.messaging.ktx.messaging
import org.koin.core.component.KoinComponent

class TriggerWorkerMessagingService : FirebaseMessagingService(), KoinComponent {

    companion object {
        const val NOTIFICATION_ERROR_TITLE = "Appen verkar ha stött på oväntade problem!";
        const val NOTIFICATION_ERROR_MESSAGE = "Öppna appen för att synka på nytt";
        const val TRIGGER_TOPIC = "triggerValuationAlarmWorker";
    }

    private val log: Logger by injectLogger("TriggerWorkerMessagingService")
    private val sharedModel = SharedModel()

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        log.d { "Message received from: ${remoteMessage.from}" }
        val context = applicationContext

        if (remoteMessage.from?.endsWith(TRIGGER_TOPIC) == true) {
            if (sharedModel.scheduleNext()) {
                log.d { "Schedule next work..." }
                ValuationAlarmWorkerFactory(context).beginAlarmTriggerWork()
            } else {
                log.e { "Failure threshold reached. Notifying user to open app and re-sync" }
                Firebase.messaging.unsubscribeFromTopic(TRIGGER_TOPIC)
                NotificationFactory(context).makeStatusNotification(
                    NOTIFICATION_ERROR_TITLE,
                    NOTIFICATION_ERROR_MESSAGE
                )
            }
        }
    }

    override fun onNewToken(token: String) {
        log.d { "New token generated: $token" }
    }

    override fun onDeletedMessages() {
        log.d { "On deleted messages called" }
    }
}