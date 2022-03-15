package com.github.tyngstast.borsdatavaluationalarmer.android.util

import android.content.Context
import androidx.work.Constraints
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.github.tyngstast.borsdatavaluationalarmer.android.worker.ValuationAlarmWorker
import java.util.concurrent.TimeUnit

class ValuationAlarmWorkerFactory(context: Context) {

    companion object {
        private const val VALUATION_SYNC_WORK = "valuationSyncWork";
        private const val WORK_REQUEST_TAG = "ValuationSyncTag";

        private val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()
    }

    private val wm = WorkManager.getInstance(context)

    fun beginAlarmTriggerWork() {
        val workRequest = OneTimeWorkRequestBuilder<ValuationAlarmWorker>()
            .setInitialDelay(randomInitialDelay(), TimeUnit.SECONDS)
            .addTag(WORK_REQUEST_TAG)
            .setConstraints(constraints)
            .build()

        wm.beginUniqueWork(VALUATION_SYNC_WORK, ExistingWorkPolicy.REPLACE, workRequest).enqueue()
    }

    /**
     * 60 - 600 seconds -> 1 - 5 minutes.
     * Use at least 1 min to let quotes update after open.
     * Use seconds to spread out requests towards backend more.
     */
    private fun randomInitialDelay(): Long {
        return (60L..300L).random()
    }
}