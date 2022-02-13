package com.github.tyngstast.borsdatavaluationalarmer.android.worker

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.github.tyngstast.borsdatavaluationalarmer.android.R
import com.github.tyngstast.borsdatavaluationalarmer.android.ui.MainActivity
import java.util.concurrent.atomic.AtomicInteger

class NotificationFactory(private val context: Context) {

    companion object {
        private const val CHANNEL_ID = "VALUATION_ALARMER_NOTIFICATION"
        private const val CHANNEL_NAME = "Valuation Alarmer Notifications"
        private const val CHANNEL_DESCRIPTION = "Show notification from valuation alarmer"
        private val notificationId = AtomicInteger(0)
    }

    fun makeStatusNotification(title: String, message: String) {
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
            channel.enableLights(true)
            channel.enableVibration(false)
            channel.setSound(null, null)

            // Add the channel
            val notificationManager = context
                .getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            notificationManager.createNotificationChannel(channel)
        }

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