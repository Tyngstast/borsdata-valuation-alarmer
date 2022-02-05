package com.github.tyngstast.borsdatavaluationalarmer.android

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.os.StrictMode
import com.github.tyngstast.borsdatavaluationalarmer.android.ui.add.AddAlarmViewModel
import com.github.tyngstast.borsdatavaluationalarmer.android.ui.list.AlarmListViewModel
import com.github.tyngstast.borsdatavaluationalarmer.android.ui.login.LoginViewModel
import com.github.tyngstast.borsdatavaluationalarmer.initKoin
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

class MainApp : Application() {

    companion object {
        private const val VALUATION_SYNC_WORK = "valuation_sync_work";
    }

    override fun onCreate() {
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
                viewModel { LoginViewModel() }
                single<SharedPreferences> {
                    get<Context>().getSharedPreferences(
                        "VALUATION_ALARMER_SETTINGS",
                        Context.MODE_PRIVATE
                    )
                }
            }
        )

        /**
        val constraints = Constraints.Builder()
        .setRequiredNetworkType(NetworkType.CONNECTED)
        .build()

        val workerRequest = OneTimeWorkRequestBuilder<ValuationAlarmDataFetcherWorker>()
        .setConstraints(constraints)
        .build()

        WorkManager.getInstance(this)
        .beginUniqueWork(
        VALUATION_SYNC_WORK,
        ExistingWorkPolicy.KEEP,
        workerRequest
        )
        .then(OneTimeWorkRequest.from(ValuationAlarmNotificationWorker::class.java))
        .enqueue()
         */
    }
}