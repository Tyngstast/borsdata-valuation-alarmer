package com.github.tyngstast.borsdatavaluationalarmer.android.ui.add

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
                .map { Item(it.insId, it.name) }
        }.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    @OptIn(ExperimentalCoroutinesApi::class)
    val kpis: StateFlow<List<Item>> = kpiName
        .filter { it.isNotBlank() }
        .mapLatest { _kpiName ->
            kpiDao.getKpis(_kpiName)
                .sortedByDescending { it.name.startsWith(_kpiName, ignoreCase = true) }
                .take(3)
                .map { Item(it.kpidId, it.name) }
        }.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    fun addAlarm() {
        // First should always be closest, or exact, match after sorting.
        val (insId, insName) = instruments.value.first()
        val (kpiId, kpiName) = kpis.value.first()
        alarmDao.insertAlarm(insId, insName, kpiId, kpiName, kpiValue.value.toDouble(), "lte")
    }
}

data class Item(val id: Long, val name: String)