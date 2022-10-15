package com.github.tyngstast.borsdatavaluationalarmer.android.util

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.github.tyngstast.borsdatavaluationalarmer.android.MainApp.Companion.CHANNEL_ID
import com.github.tyngstast.borsdatavaluationalarmer.android.R
import com.github.tyngstast.borsdatavaluationalarmer.android.ui.MainActivity
import java.util.concurrent.atomic.AtomicInteger

class NotificationFactory(private val context: Context) {

    companion object {
        private val notificationId = AtomicInteger(0)
    }

    private val triggerNotificationTitle = context.getString(R.string.notification_alarm_triggered_title)
    private val notificationErrorTitle = context.getString(R.string.notification_error_title)
    private val notificationErrorMessage = context.getString(R.string.notification_error_message)

    fun makeErrorNotification() {
        makeStatusNotification(notificationErrorTitle, notificationErrorMessage)
    }

    fun makeAlarmTriggerNotification(message: String) {
        makeStatusNotification(triggerNotificationTitle, message)
    }

    private fun makeStatusNotification(title: String, message: String) {
        val mainActivityIntent = Intent(context, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            context, 0, mainActivityIntent, PendingIntent.FLAG_IMMUTABLE
        )

        // Create the notification
        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground_upscaled)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setSound(null)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)

        // Show the notification
        NotificationManagerCompat.from(context).notify(
            notificationId.getAndIncrement(),
            builder.build()
        )
    }
}