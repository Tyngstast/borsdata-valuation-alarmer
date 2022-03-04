package com.github.tyngstast.borsdatavaluationalarmer.android.ui.add

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.tyngstast.borsdatavaluationalarmer.FluentKpi
import com.github.tyngstast.borsdatavaluationalarmer.db.AlarmDao
import com.github.tyngstast.borsdatavaluationalarmer.db.InstrumentDao
import com.github.tyngstast.borsdatavaluationalarmer.db.KpiDao
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class AddAlarmViewModel : ViewModel(), KoinComponent {

    private val instrumentDao: InstrumentDao by inject()
    private val kpiDao: KpiDao by inject()
    private val alarmDao: AlarmDao by inject()

    private val _instruments = MutableStateFlow<List<Item>>(emptyList())
    private val _kpis = MutableStateFlow(listOf<Item>())

    val instruments = _instruments.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())
    val kpis = _kpis.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    fun updateInstruments(value: String) = viewModelScope.launch {
        if (value.isNotBlank()) {
            _instruments.value = instrumentDao.getInstruments(value)
                .sortedByDescending {
                    it.name.startsWith(value, ignoreCase = true) ||
                            it.ticker.startsWith(value, ignoreCase = true)
                }
                .sortedByDescending { it.ticker.equals(value, ignoreCase = true) }
                .take(3)
                .map { InsItem(it.insId, it.name, it.yahooId) }
        }
    }

    fun updateKpis(value: String) = viewModelScope.launch {
        if (value.isNotBlank()) {
            _kpis.value = kpiDao.getKpis(value)
                .sortedByDescending { it.name.startsWith(value, ignoreCase = true) }
                .take(3)
                .map { KpiItem(it.kpidId, it.name, FluentKpi.stringValues.contains(it.name)) }
        }
    }

    fun addAlarm(kpiValue: Double) {
        // First should always be closest, or exact, match after sorting.
        val insItem = instruments.value.first() as InsItem
        val kpiItem = kpis.value.first()
        alarmDao.insertAlarm(
            insItem.id,
            insItem.name,
            insItem.yahooId,
            kpiItem.id,
            kpiItem.name,
            kpiValue,
            "lt"
        )
    }
}

open class Item(open val id: Long, val name: String)
class InsItem(id: Long, name: String, val yahooId: String) : Item(id, name)
class KpiItem(id: Long, name: String, val fluent: Boolean) : Item(id, name)
