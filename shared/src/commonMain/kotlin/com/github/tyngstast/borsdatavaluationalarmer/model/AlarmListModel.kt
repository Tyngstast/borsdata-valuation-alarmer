package com.github.tyngstast.borsdatavaluationalarmer.model

import co.touchlab.kermit.Logger
import com.github.tyngstast.borsdatavaluationalarmer.client.BorsdataClient
import com.github.tyngstast.borsdatavaluationalarmer.db.AlarmDao
import com.github.tyngstast.borsdatavaluationalarmer.db.InstrumentDao
import com.github.tyngstast.borsdatavaluationalarmer.db.KpiDao
import com.github.tyngstast.borsdatavaluationalarmer.settings.AlarmerSettings
import com.github.tyngstast.db.Alarm
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.Clock

class AlarmListModel(
    private val log: Logger,
    private val instrumentDao: InstrumentDao,
    private val kpiDao: KpiDao,
    private val alarmDao: AlarmDao,
    private val borsdataClient: BorsdataClient,
    private val alarmerSettings: AlarmerSettings,
    private val clock: Clock
) {

    fun getAll(): Flow<List<Alarm>> = alarmDao.getAllAlarmsAsFlow()

    fun updateDisableAlarm(id: Long, disable: Boolean) {
        alarmDao.updateDisableAlarm(id, disable)
    }

    fun deleteAlarm(id: Long) {
        alarmDao.deleteAlarm(id)
    }

    suspend fun updateInstrumentsAndKpisIfStale() = coroutineScope {
        val currentTimeInMillis = clock.now().toEpochMilliseconds()
        // Default to 0 to fetch if key is missing
        val latestFetch = alarmerSettings.getLatestDataFetch()
        val oneWeekInMillis = 7 * 24 * 60 * 60 * 1000
        val stale = latestFetch + oneWeekInMillis < currentTimeInMillis

        if (!stale) {
            return@coroutineScope
        }

        log.d { "Stock data is stale, fetching new..." }

        val (instruments, kpis) = awaitAll(
            async {
                val instruments = borsdataClient.getInstruments()
                instrumentDao.resetInstruments(instruments)
                instruments
            },
            async {
                val kpis = borsdataClient.getKpis()
                kpiDao.resetKpis(kpis)
                kpis
            }
        )

        alarmerSettings.updateLatestDataFetch(currentTimeInMillis)

        log.d { "Reset Instruments and KPIs. Inserted ${instruments.size} Instruments and ${kpis.size} KPIs" }
        log.d { "Latest reset epoch: $currentTimeInMillis" }
    }

    fun resetFailureCounter() {
        alarmerSettings.resetFailureCounter()
    }
}