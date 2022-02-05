package com.github.tyngstast.borsdatavaluationalarmer.android.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import co.touchlab.kermit.Logger
import com.github.tyngstast.borsdatavaluationalarmer.BorsdataApi
import com.github.tyngstast.borsdatavaluationalarmer.db.AlarmDao
import com.github.tyngstast.borsdatavaluationalarmer.injectLogger
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class ValuationAlarmDataFetcherWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params), KoinComponent {

    private val log: Logger by injectLogger("ValuationAlarmDataFetcherWorker")
    private val alarmDao: AlarmDao by inject()
    private val borsdataApi: BorsdataApi by inject()

    override suspend fun doWork(): Result {
        log.i { "doWork" }

        return try {
            val alarms = alarmDao.getAllAlarms()

            log.i {"Alarms: $alarms" }

            val triggeredAlarms = alarms
                .map {
                    log.i { "Fecthing data for ${it.insName} KPI ${it.kpiId}" }
                    val response = borsdataApi.getLatestValue(it.insId, it.kpiId)
                    val kpiValue = response.value.n
                    it to kpiValue
                }
                .filter { (alarm, kpiValue) -> kpiValue.compareTo(alarm.kpiValue) <= 0 }
                .map { (alarm, kpiValue) ->
                    log.i { "Triggered alarm: ${alarm.insName} KPI ${alarm.kpiId}" }
                    alarm.id.toString() to kpiValue
                }
                .toTypedArray()

            if (triggeredAlarms.isEmpty()) {
                log.i { "No triggered Alarms" }
            }

            Result.success(workDataOf(*triggeredAlarms))
        } catch (e: Throwable) {
            log.e(e) { e.message.toString() }
            Result.failure()
        }
    }
}