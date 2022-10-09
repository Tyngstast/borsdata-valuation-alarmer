package com.github.tyngstast.borsdatavaluationalarmer

import co.touchlab.kermit.Logger
import com.github.tyngstast.borsdatavaluationalarmer.client.BorsdataClient
import com.github.tyngstast.borsdatavaluationalarmer.client.YahooClient
import com.github.tyngstast.borsdatavaluationalarmer.db.AlarmDao
import com.github.tyngstast.borsdatavaluationalarmer.model.FluentKpi
import com.github.tyngstast.borsdatavaluationalarmer.model.ResetAppException
import com.github.tyngstast.borsdatavaluationalarmer.settings.AlarmerSettings
import com.github.tyngstast.borsdatavaluationalarmer.settings.Vault
import com.github.tyngstast.db.Alarm
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.ResponseException
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

class ValuationAlarmWorkerModel(
    private val log: Logger,
    private val alarmDao: AlarmDao,
    private val borsdataClient: BorsdataClient,
    private val yahooClient: YahooClient,
    private val alarmerSettings: AlarmerSettings,
    private val vault: Vault,
    private val clock: Clock
) {

    suspend fun triggeredAlarms(): List<Pair<Alarm, Double>> = coroutineScope {
        val alarms = alarmDao.getAllEnabledAlarms()
        log.d { "Enabled alarms: $alarms" }

        val alarmsToRun = alarms.filter(shouldRun)
        log.d { "Alarms to run: $alarmsToRun" }

        val triggeredAlarms = alarmsToRun
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
                        throw ResetAppException("401 response. Cleared API Key", e)
                    } else {
                        alarmerSettings.incrementFailureCounter()
                        throw e
                    }
                } catch (e: Throwable) {
                    alarmerSettings.incrementFailureCounter()
                    throw e
                }
            }
            // Update before filtering. We should not keep fetching just because alarm did not trigger
            .onEach { updateLastRun(it.first.id) }
            .filter { (alarm, kpiValue) -> kpiValue.compareTo(alarm.kpiValue) <= 0 }
            .map { (alarm, kpiValue) -> alarm to kpiValue }
            .also { alarmerSettings.resetFailureCounter() }

        log.d {
            if (triggeredAlarms.isEmpty()) "No triggered Alarms"
            else "triggered alarms: ${triggeredAlarms.map { it.first.insName }}"
        }

        triggeredAlarms
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
            FluentKpi.EV_OP.value -> {
                val op = borsdataClient.getLatestValue(insId, FluentKpi.EV_OP.denominatorId)
                ev() / op
            }
            FluentKpi.EV_S.value -> {
                val sales = borsdataClient.getLatestValue(insId, FluentKpi.EV_S.denominatorId)
                ev() / sales
            }
            else -> borsdataClient.getLatestValue(insId, kpiId)
        }
    }

    private fun updateLastRun(id: Long) {
        val today = today().toString()
        alarmerSettings.updateLastRun(id, today)
    }

    private val shouldRun: (Alarm) -> Boolean = { alarm: Alarm ->
        val lastRun = alarmerSettings.getLastRun(alarm.id)
        FluentKpi.stringValues.contains(alarm.kpiName) || lastRun == null || lastRun < today()
    }

    private fun today() = clock.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
}

