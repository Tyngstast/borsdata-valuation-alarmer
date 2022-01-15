package com.github.tyngstast.borsdatavaluationalarmer.android.worker

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.media.AudioAttributes
import android.media.RingtoneManager
import android.media.RingtoneManager.getDefaultUri
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.github.tyngstast.borsdatavaluationalarmer.Dao
import com.github.tyngstast.borsdatavaluationalarmer.DatabaseDriverFactory
import com.github.tyngstast.borsdatavaluationalarmer.android.R

class ValuationAlarmNotificationWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    companion object {
        private const val TAG = "ValuationAlarmNotificationWorker"
        private const val CHANNEL_ID = "VALUATION_ALARMER_ALARM_TRIGGER_NOTIFICATION"
        private const val CHANNEL_NAME = "Valuation Alarmer WorkManager Alarm Notifications"
        private const val CHANNEL_DESCRIPTION =
            "Show notification whenever valuation alarm condition is triggered"
        private const val NOTIFICATION_ID = 1
        private const val NOTIFICATION_TITLE = "Alarm triggered!"
    }

    private val dao = Dao(DatabaseDriverFactory(applicationContext))

    override suspend fun doWork(): Result {
        Log.i(TAG, "doWork")
        val context = applicationContext

        sleep()
        Log.i(TAG, "Done sleeping")

        return try {
            inputData.keyValueMap
                .map { (alarmId, kpiValue) ->
                    alarmId to kpiValue.toString()
                }
                .map { (id, kpiValue) ->
                    val alarm = dao.getAlarm(id.toLong())
                    if (alarm == null) {
                        Log.e(TAG, "Failed to find alarm with ID: $id")
                        return Result.failure()
                    }

                    alarm to kpiValue
                }
                .forEach {
                    val alarm = it.first

                    val message = "Alarm triggered from ${alarm.insName}: ${alarm.kpiName} ${alarm.operation} ${alarm.kpiValue}"
                    makeStatusNotification(message, context)

                    Log.i(TAG, "Cleaning up triggered alarm: $alarm")
//                    dao.deleteAlarm(alarm.id)
                }

            return Result.success()
        } catch (e: Throwable) {
            Log.e(TAG, e.message, e)
            Result.failure()
        }
    }

    private fun makeStatusNotification(message: String, context: Context) {
        // Make a channel if necessary
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Create the NotificationChannel, but only on API 26+ because
            // the NotificationChannel class is new and not in the support library
            val channel =
                NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH)
            channel.description = CHANNEL_DESCRIPTION
            channel.enableVibration(true)
            channel.enableLights(true)
            channel.vibrationPattern = longArrayOf(200, 200, 200)

            val ringtoneManager = getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            val audioAttributes = AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_NOTIFICATION_RINGTONE)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build()

            channel.setSound(ringtoneManager, audioAttributes)

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
            .setVibrate(longArrayOf(200, 200, 200))

        // Show the notification
        NotificationManagerCompat.from(context).notify(NOTIFICATION_ID, builder.build())
    }

    private fun sleep() {
        try {
            Thread.sleep(4000, 0)
        } catch (e: InterruptedException) {
            Log.e(TAG, e.message.toString())
        }
    }
}