package com.github.tyngstast.borsdatavaluationalarmer.android.ui.add

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.tyngstast.borsdatavaluationalarmer.model.AddAlarmModel
import com.github.tyngstast.borsdatavaluationalarmer.model.InsItem
import com.github.tyngstast.borsdatavaluationalarmer.model.Item
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class AddAlarmViewModel(private val addAlarmModel: AddAlarmModel) : ViewModel() {

    private val _instruments = MutableStateFlow<List<Item>>(emptyList())
    private val _kpis = MutableStateFlow(listOf<Item>())

    val instruments = _instruments.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())
    val kpis = _kpis.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    fun getInstruments(value: String) = viewModelScope.launch {
        _instruments.value = addAlarmModel.getSortedInstruments(value)
    }

    fun getKpis(value: String) = viewModelScope.launch {
        _kpis.value = addAlarmModel.getSortedKpis(value)
    }

    fun addAlarm(kpiValue: Double) = viewModelScope.launch {
        // First should always be closest, or exact, match after sorting.
        val insItem = instruments.value.first() as InsItem
        val kpiItem = kpis.value.first()
        addAlarmModel.addAlarm(kpiValue, insItem, kpiItem)
    }
}
