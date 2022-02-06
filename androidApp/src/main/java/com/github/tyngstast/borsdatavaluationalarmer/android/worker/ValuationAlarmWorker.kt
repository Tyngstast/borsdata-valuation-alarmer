package com.github.tyngstast.borsdatavaluationalarmer.android.worker

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import co.touchlab.kermit.Logger
import com.github.tyngstast.borsdatavaluationalarmer.SharedModel
import com.github.tyngstast.borsdatavaluationalarmer.android.R
import com.github.tyngstast.borsdatavaluationalarmer.injectLogger
import org.koin.core.component.KoinComponent
import java.util.concurrent.atomic.AtomicInteger

class ValuationAlarmWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params), KoinComponent {

    companion object {
        private const val CHANNEL_ID = "VALUATION_ALARMER_ALARM_TRIGGER_NOTIFICATION"
        private const val CHANNEL_NAME = "Valuation Alarmer WorkManager Alarm Notifications"
        private const val CHANNEL_DESCRIPTION =
            "Show notification whenever valuation alarm condition is triggered"
        private const val NOTIFICATION_TITLE = "Värderingslarm triggades!"
        private val notificationId = AtomicInteger(0)
    }

    private val log: Logger by injectLogger("ValuationAlarmWorker")
    private val sharedModel = SharedModel()

    override suspend fun doWork(): Result {
        log.i { "doWork" }
        val context = applicationContext

        val result: kotlin.Result<Result> = kotlin.runCatching {
            val triggeredAlarms = sharedModel.triggeredAlarms()
            log.i { "triggered alarms: ${triggeredAlarms.map { it.first.insName }}" }

            triggeredAlarms.forEach {
                val alarm = it.first
                val kpiValue = String.format("%.1f", it.second)

                val message = "${alarm.insName}: ${alarm.kpiName} $kpiValue under ${alarm.kpiValue}"
                makeStatusNotification(message, context)
            }

            Result.success()
        }.onSuccess {
            // Trigger from self instead of periodic to enable a more tailored schedule
            WorkerFactory(context).enqueueNextReplace()
        }.onFailure {
            log.e(it) { it.message.toString() }
            Result.failure()
        }

        return result.getOrDefault(Result.failure())
    }

    private fun makeStatusNotification(message: String, context: Context) {
        // Make a channel if necessary
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Create the NotificationChannel, but only on API 26+ because
            // the NotificationChannel class is new and not in the support library
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            )
            channel.description = CHANNEL_DESCRIPTION
            channel.enableVibration(true)
            channel.enableLights(true)
            channel.vibrationPattern = longArrayOf(300)

            // Add the channel
            val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            notificationManager.createNotificationChannel(channel)
        }

        // Create the notification
        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(NOTIFICATION_TITLE)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setVibrate(longArrayOf(300))

        // Show the notification
        NotificationManagerCompat.from(context).notify(
            notificationId.getAndIncrement(),
            builder.build()
        )
    }
}