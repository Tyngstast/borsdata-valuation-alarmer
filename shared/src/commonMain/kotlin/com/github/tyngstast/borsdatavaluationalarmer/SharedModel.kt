package com.github.tyngstast.borsdatavaluationalarmer

import co.touchlab.kermit.Logger
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
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
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

    fun getNextAlarmTriggerWorkInitialDelay(): Long {
        val tz = TimeZone.currentSystemDefault()
        val now = clock.now()
        val nowLdt = now.toLocalDateTime(tz)
        log.i { "current dateTime: $nowLdt" }
        val (day, hour) = when {
            // Disable for testing 2022-02-05 - 2022-05-06
//            // Don't run on sundays
//            nowLdt.dayOfWeek == DayOfWeek.SUNDAY -> nowLdt.dayOfMonth + 1 to 8
//            // We only want to run once at 8 on saturdays, so after 8 -> monday morning
//            nowLdt.dayOfWeek == DayOfWeek.SATURDAY && nowLdt.hour > 8 -> nowLdt.dayOfMonth + 2 to 8
            // run next day at 8
            nowLdt.hour > 18 -> nowLdt.dayOfMonth + 1 to 8
            // run at 8
            nowLdt.hour <= 8 -> nowLdt.dayOfMonth to 8
            // run next hour full hour
            else -> nowLdt.dayOfMonth to nowLdt.hour + 1
        }

        val next = LocalDateTime(nowLdt.year, nowLdt.monthNumber, day, hour, 0, 0)
        log.i { "next alarm trigger: $next" }

        return next.toInstant(tz).toEpochMilliseconds() - now.toEpochMilliseconds()
    }

    suspend fun triggeredAlarms(): List<Pair<Alarm, Double>> = coroutineScope {
        val alarms = alarmDao.getAllAlarms()

        log.i { "Alarms: ${alarms.size}" }

        val triggeredAlarms = alarms
            .map {
                val kpiValue = async {
                    val response = borsdataApi.getLatestValue(it.insId, it.kpiId)
                    response.value.n
                }
                it to kpiValue
            }
            .map { (alarm, kpiValueDeferred) ->
                try {
                    alarm to kpiValueDeferred.await()
                } catch (e: Throwable) {
                    if (e is ClientRequestException && e.response.status == HttpStatusCode.Unauthorized) {
                        vault.clearApiKey()
                    }
                    throw e
                }
            }
            .filter { (alarm, kpiValue) -> kpiValue.compareTo(alarm.kpiValue) <= 0 }
            .map { (alarm, kpiValue) -> alarm to kpiValue }

        if (triggeredAlarms.isEmpty()) {
            log.i { "No triggered Alarms" }
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