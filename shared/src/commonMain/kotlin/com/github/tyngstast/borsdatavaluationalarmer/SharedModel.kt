package com.github.tyngstast.borsdatavaluationalarmer

import co.touchlab.kermit.Logger
import com.github.tyngstast.borsdatavaluationalarmer.client.BorsdataClient
import com.github.tyngstast.borsdatavaluationalarmer.client.YahooClient
import com.github.tyngstast.borsdatavaluationalarmer.db.AlarmDao
import com.github.tyngstast.borsdatavaluationalarmer.db.InstrumentDao
import com.github.tyngstast.borsdatavaluationalarmer.db.KpiDao
import com.github.tyngstast.db.Alarm
import com.russhwolf.settings.Settings
import io.ktor.client.features.*
import io.ktor.http.*
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.datetime.Clock
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class SharedModel : KoinComponent {
    companion object {
        private const val DB_STOCK_DATA_TIMESTAMP_KEY = "DbStockDataTimestampKey"
        private const val WORKER_FAILURE_COUNTER = "WorkerFailureCounter"
        private const val FAILURE_THRESHOLD: Int = 3
    }

    private val log: Logger by injectLogger("SharedModel")
    private val instrumentDao: InstrumentDao by inject()
    private val kpiDao: KpiDao by inject()
    private val alarmDao: AlarmDao by inject()
    private val borsdataClient: BorsdataClient by inject()
    private val yahooClient: YahooClient by inject()
    private val settings: Settings by inject()
    private val vault: Vault by inject()
    private val clock: Clock by inject()

    suspend fun triggeredAlarms(): List<Pair<Alarm, Double>> = coroutineScope {
        val alarms = alarmDao.getAllEnabledAlarms()

        log.d { "Enabled alarms: $alarms" }

        val triggeredAlarms = alarms
            .map {
                val kpiValue = async {
                    calcOrGetKpiValue(it.kpiId, it.kpiName, it.insId, it.yahooId)
                }
                it to kpiValue
            }
            .map { (alarm, kpiValueDeferred) ->
                try {
                    alarm to kpiValueDeferred.await()
                } catch (e: ResponseException) {
                    if (e is ClientRequestException && e.response.status == HttpStatusCode.Unauthorized) {
                        vault.clearApiKey()
                    } else {
                        incrementFailureCounter()
                    }
                    throw e
                } catch (e: Throwable) {
                    incrementFailureCounter()
                    throw e
                }
            }
            .filter { (alarm, kpiValue) -> kpiValue.compareTo(alarm.kpiValue) <= 0 }
            .map { (alarm, kpiValue) -> alarm to kpiValue }
            .also { resetFailureCounter() }

        log.d {
            if (triggeredAlarms.isEmpty()) "No triggered Alarms"
            else "triggered alarms: ${triggeredAlarms.map { it.first.insName }}"
        }

        triggeredAlarms
    }

    suspend fun updateInstrumentsAndKpisIfStale() = coroutineScope {
        val currentTimeInMillis = clock.now().toEpochMilliseconds()
        // Default to 0 to fetch if key is missing
        val latestFetch = settings.getLong(DB_STOCK_DATA_TIMESTAMP_KEY, 0)
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

        settings.putLong(DB_STOCK_DATA_TIMESTAMP_KEY, currentTimeInMillis)

        log.d { "Reset Instruments and KPIs. Inserted ${instruments.size} Instruments and ${kpis.size} KPIs" }
        log.d { "Latest reset epoch: $currentTimeInMillis" }
    }

    private suspend fun calcOrGetKpiValue(
        kpiId: Long,
        kpiName: String,
        insId: Long,
        yahooId: String
    ): Double {
        val price: Double = try {
            yahooClient.getLatestPrice(yahooId)
        } catch (e: Throwable) {
            log.e { "Failed to get price from yahoo, fall back to pre-calcuated from BD. Error: ${e.message}" }
            return borsdataClient.getLatestValue(insId, kpiId)
        }

        suspend fun ev(): Double = coroutineScope {
            val (shares, netDebt) = awaitAll(
                async { borsdataClient.getLatestValue(insId, 61) },
                async { borsdataClient.getLatestValue(insId, 60) }
            )
            ((price * shares) + netDebt)
        }

        return when (kpiName) {
            FluentKpi.P_E.value -> {
                val eps = borsdataClient.getLatestValue(insId, FluentKpi.P_E.denominatorId)
                price / eps
            }
            FluentKpi.EV_E.value -> {
                val earnings = borsdataClient.getLatestValue(insId, FluentKpi.EV_E.denominatorId)
                ev() / earnings
            }
            FluentKpi.EV_EBIT.value -> {
                val ebit = borsdataClient.getLatestValue(insId, FluentKpi.EV_EBIT.denominatorId)
                ev() / ebit
            }
            FluentKpi.EV_EBITDA.value -> {
                val ebitda = borsdataClient.getLatestValue(insId, FluentKpi.EV_EBITDA.denominatorId)
                ev() / ebitda
            }
            FluentKpi.EV_FCF.value -> {
                val fcf = borsdataClient.getLatestValue(insId, FluentKpi.EV_FCF.denominatorId)
                ev() / fcf
            }
            FluentKpi.EV_S.value -> {
                val sales = borsdataClient.getLatestValue(insId, FluentKpi.EV_S.denominatorId)
                ev() / sales
            }
            else -> borsdataClient.getLatestValue(insId, kpiId)
        }
    }

    fun scheduleNext(): Boolean {
        val key = vault.getApiKey()
        val failures = settings.getInt(WORKER_FAILURE_COUNTER, 0)
        return !key.isNullOrBlank() && failures < FAILURE_THRESHOLD
    }

    private fun incrementFailureCounter() {
        val failures: Int = settings.getInt(WORKER_FAILURE_COUNTER, 0)
        settings.putInt(WORKER_FAILURE_COUNTER, failures + 1)
    }

    private fun resetFailureCounter() {
        settings.putInt(WORKER_FAILURE_COUNTER, 0)
    }
}