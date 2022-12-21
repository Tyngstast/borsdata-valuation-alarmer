package com.github.tyngstast.borsdatavaluationalarmer.model

import com.github.tyngstast.borsdatavaluationalarmer.db.AlarmDao
import com.github.tyngstast.borsdatavaluationalarmer.db.InstrumentDao
import com.github.tyngstast.borsdatavaluationalarmer.db.KpiDao

class AddAlarmModel(
    private val instrumentDao: InstrumentDao,
    private val kpiDao: KpiDao,
    private val alarmDao: AlarmDao,
    private val appLanguage: AppLanguage
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

        val kpis = if (appLanguage == AppLanguage.SV) kpiDao.getKpis(value) else kpiDao.getKpisEn(value)

        return kpis
            .sortedByDescending { it.name.startsWith(value, ignoreCase = true) }
            .take(3)
            .map {
                KpiItem(
                    id = it.kpidId,
                    name = if (appLanguage == AppLanguage.SV || it.nameEn == null) it.name else it.nameEn,
                    fluent = FluentKpi.stringValues.contains(it.name),
                    type = KpiType.fromString(it.type)
                )
            }
    }

    fun addAlarm(kpiValue: Double, instrument: InsItem, kpi: Item, isAbove: Boolean) {
        alarmDao.insertAlarm(
            instrument.id,
            instrument.name,
            instrument.yahooId,
            kpi.id,
            kpi.name,
            kpiValue,
            if (isAbove) "gt" else "lt"
        )
    }
}

open class Item(val id: Long, val name: String)
class InsItem(id: Long, name: String, val yahooId: String) : Item(id, name)
class KpiItem(id: Long, name: String, val fluent: Boolean, val type: KpiType) : Item(id, name)
