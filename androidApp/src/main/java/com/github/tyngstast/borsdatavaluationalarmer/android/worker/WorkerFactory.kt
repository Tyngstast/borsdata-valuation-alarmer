package com.github.tyngstast.borsdatavaluationalarmer.android.worker

import android.content.Context
import androidx.work.Constraints
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequest
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.github.tyngstast.borsdatavaluationalarmer.SharedModel
import java.util.concurrent.TimeUnit

class WorkerFactory(context: Context) {

    companion object {
        private const val VALUATION_SYNC_WORK = "valuationSyncWork";
        private const val WORK_REQUEST_TAG = "ValuationSyncTag";

        private val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()
    }

    private val sharedModel = SharedModel()
    private val wm = WorkManager.getInstance(context)

    private val workerRequest: OneTimeWorkRequest =
        OneTimeWorkRequestBuilder<ValuationAlarmWorker>()
            .addTag(WORK_REQUEST_TAG)
            .setInitialDelay(
                sharedModel.getNextAlarmTriggerWorkInitialDelay(),
                TimeUnit.MILLISECONDS
            )
            .setConstraints(constraints)
            .build()

    fun enqueueNextKeep() = workerRequest.run {
        enqueueNext(ExistingWorkPolicy.KEEP, this)
    }

    fun enqueueNextReplace() = workerRequest.run {
        enqueueNext(ExistingWorkPolicy.REPLACE, this)
    }

    private fun enqueueNext(existingWorkPolicy: ExistingWorkPolicy, workRequest: OneTimeWorkRequest) {
        wm.enqueueUniqueWork(VALUATION_SYNC_WORK, existingWorkPolicy, workRequest)
    }
}