package com.github.tyngstast.borsdatavaluationalarmer.android.ui.add

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.tyngstast.borsdatavaluationalarmer.FluentKpi
import com.github.tyngstast.borsdatavaluationalarmer.db.AlarmDao
import com.github.tyngstast.borsdatavaluationalarmer.db.InstrumentDao
import com.github.tyngstast.borsdatavaluationalarmer.db.KpiDao
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.stateIn
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class AddAlarmViewModel : ViewModel(), KoinComponent {

    private val instrumentDao: InstrumentDao by inject()
    private val kpiDao: KpiDao by inject()
    private val alarmDao: AlarmDao by inject()

    private val insName = MutableStateFlow("")
    private val kpiName = MutableStateFlow("")
    private val kpiValue = MutableStateFlow("")

    fun setInsName(value: String) {
        insName.value = value
    }

    fun setKpiName(value: String) {
        kpiName.value = value
    }

    fun setKpiValue(value: String) {
        kpiValue.value = value
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val instruments: StateFlow<List<Item>> = insName
        .filter { it.isNotBlank() }
        .mapLatest { _insName ->
            instrumentDao.getInstruments(_insName)
                .sortedByDescending {
                    it.name.startsWith(_insName, ignoreCase = true) ||
                            it.ticker.startsWith(_insName, ignoreCase = true)
                }
                .sortedByDescending { it.ticker.equals(_insName, ignoreCase = true) }
                .take(3)
                .map { InsItem(it.insId, it.name, it.yahooId) }
        }.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    @OptIn(ExperimentalCoroutinesApi::class)
    val kpis: StateFlow<List<Item>> = kpiName
        .filter { it.isNotBlank() }
        .mapLatest { _kpiName ->
            kpiDao.getKpis(_kpiName)
                .sortedByDescending { it.name.startsWith(_kpiName, ignoreCase = true) }
                .take(3)
                .map { KpiItem(it.kpidId, it.name, FluentKpi.stringValues.contains(it.name)) }
        }.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    fun addAlarm() {
        // First should always be closest, or exact, match after sorting.
        val insItem = instruments.value.first() as InsItem
        val kpiItem = kpis.value.first()
        alarmDao.insertAlarm(
            insItem.id,
            insItem.name,
            insItem.yahooId,
            kpiItem.id,
            kpiItem.name,
            kpiValue.value.toDouble(),
            "lte"
        )
    }
}

open class Item(open val id: Long, val name: String)
class InsItem(id: Long, name: String, val yahooId: String) : Item(id, name)
class KpiItem(id: Long, name: String, val fluent: Boolean) : Item(id, name)
