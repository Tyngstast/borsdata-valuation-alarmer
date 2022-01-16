package com.github.tyngstast.borsdatavaluationalarmer.android

import android.app.Application
import android.content.Context
import com.github.tyngstast.borsdatavaluationalarmer.initKoin
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

class MainApp : Application() {

    companion object {
        private const val VALUATION_SYNC_WORK = "valuation_sync_work";
    }

    override fun onCreate() {
        super.onCreate()
        initKoin(
            module {
                viewModel { AlarmViewModel() }
                single<Context> { this@MainApp }
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