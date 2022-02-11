package com.github.tyngstast.borsdatavaluationalarmer.android.worker

import android.content.Context
import androidx.work.Constraints
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager

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
            .addTag(WORK_REQUEST_TAG)
            .setConstraints(constraints)
            .build()

        wm.beginUniqueWork(VALUATION_SYNC_WORK, ExistingWorkPolicy.REPLACE, workRequest).enqueue()
    }
}