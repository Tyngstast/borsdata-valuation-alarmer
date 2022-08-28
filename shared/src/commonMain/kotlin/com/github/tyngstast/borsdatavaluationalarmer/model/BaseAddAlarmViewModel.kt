package com.github.tyngstast.borsdatavaluationalarmer.model

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

open class BaseAddAlarmViewModel(private val addAlarmModel: AddAlarmModel) : ViewModel() {

    private val _instruments = MutableStateFlow<List<Item>>(emptyList())
    private val _kpis = MutableStateFlow(listOf<Item>())

    val instrumentStateFlow: StateFlow<List<Item>> = _instruments
    val kpiStateFlow: StateFlow<List<Item>> = _kpis

    fun getInstruments(value: String) = viewModelScope.launch {
        _instruments.value = addAlarmModel.getSortedInstruments(value)
    }

    fun getKpis(value: String) = viewModelScope.launch {
        _kpis.value = addAlarmModel.getSortedKpis(value)
    }

    fun addAlarm(kpiValue: Double) = viewModelScope.launch {
        // First should always be closest, or exact, match after sorting.
        val insItem = instrumentStateFlow.value.first() as InsItem
        val kpiItem = kpiStateFlow.value.first()
        addAlarmModel.addAlarm(kpiValue, insItem, kpiItem)
    }
}