package com.github.tyngstast.borsdatavaluationalarmer.android

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.os.StrictMode
import com.github.tyngstast.borsdatavaluationalarmer.android.ui.add.AddAlarmViewModel
import com.github.tyngstast.borsdatavaluationalarmer.android.ui.edit.EditAlarmViewModel
import com.github.tyngstast.borsdatavaluationalarmer.android.ui.list.AlarmListViewModel
import com.github.tyngstast.borsdatavaluationalarmer.android.ui.login.LoginViewModel
import com.github.tyngstast.borsdatavaluationalarmer.initKoin
import com.google.firebase.crashlytics.FirebaseCrashlytics
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

class MainApp : Application() {

    override fun onCreate() {
        FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(!BuildConfig.DEBUG)

        if (BuildConfig.DEBUG) {
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
                viewModel { AlarmListViewModel() }
                viewModel { AddAlarmViewModel() }
                viewModel { EditAlarmViewModel() }
                viewModel { LoginViewModel() }
                single<SharedPreferences> {
                    get<Context>().getSharedPreferences(
                        "VALUATION_ALARMER_SETTINGS",
                        Context.MODE_PRIVATE
                    )
                }
            }
        )
    }
}