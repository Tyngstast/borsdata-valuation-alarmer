package com.github.tyngstast.borsdatavaluationalarmer

import co.touchlab.kermit.Logger
import com.github.tyngstast.borsdatavaluationalarmer.db.AlarmDao
import com.github.tyngstast.borsdatavaluationalarmer.db.InstrumentDao
import com.github.tyngstast.borsdatavaluationalarmer.db.KpiDao
import com.russhwolf.settings.Settings
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.datetime.Clock
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class SharedModel : KoinComponent {
    companion object {
        private const val DB_STOCK_DATA_TIMESTAMP_KEY = "DbStockDataTimestampKey"
    }

    private val log: Logger by injectLogger("SharedModel")
    private val instrumentDao: InstrumentDao by inject()
    private val kpiDao: KpiDao by inject()
    private val alarmDao: AlarmDao by inject()
    private val borsdataApi: BorsdataApi by inject()
    private val settings: Settings by inject()
    private val vault: Vault by inject()
    private val clock: Clock by inject()

    suspend fun updateInstrumentsAndKpisIfStale() = coroutineScope {
        val currentTimeInMillis = clock.now().toEpochMilliseconds()
        // Default to 0 to fetch if key is missing
        val latestFetch = settings.getLong(DB_STOCK_DATA_TIMESTAMP_KEY, 0)
        val oneWeekInMillis = 7 * 24 * 60 * 60 * 1000
        val stale = latestFetch + oneWeekInMillis < currentTimeInMillis

        if (!stale) {
            return@coroutineScope
        }

        log.i { "Stock data is stale, fetching new..." }

        val (instruments, kpis) = awaitAll(
            async {
                val instruments: List<InstrumentDto> = borsdataApi.getInstruments()
                instrumentDao.resetInstruments(instruments)
                instruments
            },
            async {
                val kpis = borsdataApi.getKpis()
                kpiDao.resetKpis(kpis)
                kpis
            }
        )

        settings.putLong(DB_STOCK_DATA_TIMESTAMP_KEY, currentTimeInMillis)

        log.i { "Reset Instruments and KPIs. Inserted ${instruments.size} Instruments and ${kpis.size} KPIs" }
        log.i { "Latest reset epoch: $currentTimeInMillis" }
    }
}