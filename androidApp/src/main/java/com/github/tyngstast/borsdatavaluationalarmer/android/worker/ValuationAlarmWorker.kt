package com.github.tyngstast.borsdatavaluationalarmer.android.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import co.touchlab.kermit.Logger
import com.github.tyngstast.borsdatavaluationalarmer.SharedModel
import com.github.tyngstast.borsdatavaluationalarmer.android.util.NotificationFactory
import com.github.tyngstast.borsdatavaluationalarmer.injectLogger
import org.koin.core.component.KoinComponent

class ValuationAlarmWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params), KoinComponent {

    companion object {
        private const val NOTIFICATION_TITLE = "Värderingslarm triggades!"
    }

    private val log: Logger by injectLogger("ValuationAlarmWorker")
    private val sharedModel = SharedModel()

    override suspend fun doWork(): Result {
        log.d { "doWork" }
        val context = applicationContext

        val result = kotlin.runCatching {
            val triggeredAlarms = sharedModel.triggeredAlarms()

            triggeredAlarms.forEach {
                val alarm = it.first
                val kpiValue = String.format("%.1f", it.second)

                val message = "${alarm.insName}: ${alarm.kpiName} $kpiValue under ${alarm.kpiValue}"
                NotificationFactory(context).makeStatusNotification(NOTIFICATION_TITLE, message)
            }

            Result.success()
        }.onFailure {
            log.e(it) { "Error running worker: ${it.message.toString()}" }
            Result.failure()
        }

        return result.getOrDefault(Result.failure())
    }
}