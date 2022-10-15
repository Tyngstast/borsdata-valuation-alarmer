package com.github.tyngstast.borsdatavaluationalarmer.android

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import android.os.StrictMode
import com.github.tyngstast.borsdatavaluationalarmer.android.ui.add.AddAlarmViewModel
import com.github.tyngstast.borsdatavaluationalarmer.android.ui.edit.EditAlarmViewModel
import com.github.tyngstast.borsdatavaluationalarmer.android.ui.list.AlarmListViewModel
import com.github.tyngstast.borsdatavaluationalarmer.android.ui.login.LoginViewModel
import com.github.tyngstast.borsdatavaluationalarmer.getWith
import com.github.tyngstast.borsdatavaluationalarmer.initKoin
import com.github.tyngstast.borsdatavaluationalarmer.isDebug
import com.google.firebase.crashlytics.FirebaseCrashlytics
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

@Suppress("unused")
class MainApp : Application() {

    companion object {
        const val CHANNEL_ID = "VALUATION_ALARMER_NOTIFICATION"
        const val CHANNEL_NAME = "Valuation Alarmer Notifications"
        const val CHANNEL_DESCRIPTION = "Show notification from valuation alarmer"
    }

    override fun onCreate() {
        FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(!isDebug)

        if (isDebug) {
            StrictMode.setThreadPolicy(
                StrictMode.ThreadPolicy.Builder()
                    .detectNetwork()
                    .penaltyLog()
                    .build()
            )
            StrictMode.setVmPolicy(
                StrictMode.VmPolicy.Builder()
                    .detectLeakedSqlLiteObjects()
                    .detectLeakedClosableObjects()
                    .penaltyLog()
                    .build()
            )
        }

        super.onCreate()

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
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }

        initKoin(
            module {
                single<Context> { this@MainApp }
                single<SharedPreferences> {
                    get<Context>().getSharedPreferences(
                        "VALUATION_ALARMER_SETTINGS",
                        Context.MODE_PRIVATE
                    )
                }
                viewModel { AlarmListViewModel(getWith("AlarmListViewModel"), get()) }
                viewModel { AddAlarmViewModel(get()) }
                viewModel { EditAlarmViewModel(get()) }
                viewModel { LoginViewModel(get()) }
            }
        )
    }
}