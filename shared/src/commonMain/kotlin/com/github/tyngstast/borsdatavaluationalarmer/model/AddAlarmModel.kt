package com.github.tyngstast.borsdatavaluationalarmer.model

import com.github.tyngstast.borsdatavaluationalarmer.db.AlarmDao
import com.github.tyngstast.borsdatavaluationalarmer.db.InstrumentDao
import com.github.tyngstast.borsdatavaluationalarmer.db.KpiDao

class AddAlarmModel(
    private val instrumentDao: InstrumentDao,
    private val kpiDao: KpiDao,
    private val alarmDao: AlarmDao,
) {

    suspend fun getSortedInstruments(value: String): List<Item> {
        if (value.isBlank()) {
            return emptyList()
        }

        return instrumentDao.getInstruments(value)
            .sortedByDescending {
                it.name.startsWith(value, ignoreCase = true) ||
                        it.ticker.startsWith(value, ignoreCase = true)
            }
            .sortedByDescending { it.ticker.equals(value, ignoreCase = true) }
            .take(3)
            .map { InsItem(it.insId, it.name, it.yahooId) }
    }

    suspend fun getSortedKpis(value: String): List<Item> {
        if (value.isBlank()) {
            return emptyList()
        }

        return kpiDao.getKpis(value)
            .sortedByDescending { it.name.startsWith(value, ignoreCase = true) }
            .take(3)
            .map { KpiItem(it.kpidId, it.name, FluentKpi.stringValues.contains(it.name)) }
    }

    fun addAlarm(kpiValue: Double, instrument: InsItem, kpi: Item) {
        alarmDao.insertAlarm(
            instrument.id,
            instrument.name,
            instrument.yahooId,
            kpi.id,
            kpi.name,
            kpiValue,
            "lt"
        )
    }
}

open class Item(open val id: Long, val name: String)
class InsItem(id: Long, name: String, val yahooId: String) : Item(id, name)
class KpiItem(id: Long, name: String, val fluent: Boolean) : Item(id, name)
