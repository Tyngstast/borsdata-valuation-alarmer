package com.github.tyngstast.borsdatavaluationalarmer.android.worker

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.github.tyngstast.borsdatavaluationalarmer.*

class ValuationAlarmDataFetcherWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    companion object {
        private const val TAG = "ValuationAlarmDataFetcherWorker"
    }

    private val dao = Dao(DatabaseDriverFactory(applicationContext))
    private val borsdataApi = BorsdataApi(KVaultImpl(KVaultFactory(applicationContext)))

    override suspend fun doWork(): Result {
        Log.i(TAG, "doWork")

        return try {
            val alarms = dao.getAllAlarms()

            Log.i(TAG, "Alarms: $alarms")

            val triggeredAlarms = alarms
                .map {
                    Log.i(TAG, "Fecthing data for ${it.insName} KPI ${it.kpiId}")
                    val response = borsdataApi.getLatestValue(it.insId, it.kpiId)
                    val kpiValue = response.value.n
                    it to kpiValue
                }
                .filter { (alarm, kpiValue) -> alarm.evaluate(kpiValue)}
                .map { (alarm, kpiValue) ->
                    Log.i(TAG, "Triggered alarm: ${alarm.insName} KPI ${alarm.kpiId}")
                    alarm.id.toString() to kpiValue
                }
                .toTypedArray()

            if (triggeredAlarms.isEmpty()) {
                Log.i(TAG, "No triggered Alarms")
            }

            Result.success(workDataOf(*triggeredAlarms))
        } catch (e: Throwable) {
            Log.e(TAG, e.message, e)
            Result.failure()
        }
    }
}