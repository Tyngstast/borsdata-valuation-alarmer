package com.github.tyngstast.borsdatavaluationalarmer.android.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import co.touchlab.kermit.Logger
import com.github.tyngstast.borsdatavaluationalarmer.AlarmerSdk
import com.github.tyngstast.borsdatavaluationalarmer.ResetAppException
import com.github.tyngstast.borsdatavaluationalarmer.android.messaging.TriggerWorkerMessagingService
import com.github.tyngstast.borsdatavaluationalarmer.android.util.NotificationFactory
import com.github.tyngstast.borsdatavaluationalarmer.injectLogger
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.ktx.messaging
import kotlinx.coroutines.delay
import org.koin.core.component.KoinComponent

class ValuationAlarmWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params), KoinComponent {

    private val log: Logger by injectLogger("ValuationAlarmWorker")
    private val alarmerSdk = AlarmerSdk()

    override suspend fun doWork(): Result {
        log.d { "doWork" }
        val context = applicationContext

        val result = kotlin.runCatching {
            val triggeredAlarms = alarmerSdk.triggeredAlarms()

            triggeredAlarms.forEach {
                val alarm = it.first
                val kpiValue = String.format("%.1f", it.second)

                val message = "${alarm.insName}: ${alarm.kpiName} $kpiValue under ${alarm.kpiValue}"
                NotificationFactory(context).makeAlarmTriggerNotification(message)
                delay(500)
            }

            Result.success()
        }.onFailure {
            log.e(it) { "Error running worker: ${it.message.toString()}" }
            if (it is ResetAppException) {
                log.e { "Critical error, likely 401 response. Resetting app and notifying user" }
                Firebase.messaging.unsubscribeFromTopic(TriggerWorkerMessagingService.TRIGGER_TOPIC)
                NotificationFactory(context).makeErrorNotification()
            }
            Result.failure()
        }

        return result.getOrDefault(Result.failure())
    }
}