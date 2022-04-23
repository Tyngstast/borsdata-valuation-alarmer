package com.github.tyngstast.borsdatavaluationalarmer.android.messaging

import co.touchlab.kermit.Logger
import com.github.tyngstast.borsdatavaluationalarmer.android.util.NotificationFactory
import com.github.tyngstast.borsdatavaluationalarmer.android.util.ValuationAlarmWorkerFactory
import com.github.tyngstast.borsdatavaluationalarmer.injectLogger
import com.github.tyngstast.borsdatavaluationalarmer.settings.AlarmerSettings
import com.github.tyngstast.borsdatavaluationalarmer.settings.Vault
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.firebase.messaging.ktx.messaging
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class TriggerWorkerMessagingService : FirebaseMessagingService(), KoinComponent {

    companion object {
        private const val FAILURE_THRESHOLD: Int = 3
        const val TRIGGER_TOPIC = "triggerValuationAlarmWorker";
    }

    private val log: Logger by injectLogger("TriggerWorkerMessagingService")
    private val vault: Vault by inject()
    private val alarmerSettings: AlarmerSettings by inject()

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        log.d { "Message received from: ${remoteMessage.from}" }
        val context = applicationContext

        if (remoteMessage.from?.endsWith(TRIGGER_TOPIC) == true) {
            if (scheduleNext()) {
                log.d { "Scheduling next worker execution..." }
                ValuationAlarmWorkerFactory(context).beginAlarmTriggerWork()
            } else {
                log.e { "Failure threshold reached. Notifying user to open app and re-sync" }
                Firebase.messaging.unsubscribeFromTopic(TRIGGER_TOPIC)
                NotificationFactory(context).makeErrorNotification()
            }
        }
    }

    override fun onNewToken(token: String) {
        log.d { "New token generated: $token" }
    }

    override fun onDeletedMessages() {
        log.d { "On deleted messages called" }
    }

    private fun scheduleNext(): Boolean {
        val key = vault.getApiKey()
        val failures = alarmerSettings.getFailureCount()
        return !key.isNullOrBlank() && failures < FAILURE_THRESHOLD
    }
}