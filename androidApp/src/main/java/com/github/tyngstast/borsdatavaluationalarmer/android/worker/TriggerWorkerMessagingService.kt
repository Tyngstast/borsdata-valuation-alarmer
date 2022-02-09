package com.github.tyngstast.borsdatavaluationalarmer.android.worker

import co.touchlab.kermit.Logger
import com.github.tyngstast.borsdatavaluationalarmer.SharedModel
import com.github.tyngstast.borsdatavaluationalarmer.injectLogger
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import org.koin.core.component.KoinComponent

class TriggerWorkerMessagingService : FirebaseMessagingService(), KoinComponent {

    companion object {
        const val TRIGGER_TOPIC = "triggerValuationAlarmWorker";
    }

    private val log: Logger by injectLogger("TriggerWorkerMessagingService")
    private val sharedModel = SharedModel()

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        log.d { "Message received from: ${remoteMessage.from}" }

        if (remoteMessage.data.isNotEmpty()) {
            if (sharedModel.scheduleNext()) {
                WorkerFactory(applicationContext).beginAlarmTriggerWork()
            } else {
                log.e { "Failure threshold reached. *TODO Show error notification to user probably*" }
            }
        }
    }

    override fun onNewToken(token: String) {
        log.d { "New token generated: $token" }
    }
}