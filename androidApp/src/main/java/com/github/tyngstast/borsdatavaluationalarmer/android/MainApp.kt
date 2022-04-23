package com.github.tyngstast.borsdatavaluationalarmer.android

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
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