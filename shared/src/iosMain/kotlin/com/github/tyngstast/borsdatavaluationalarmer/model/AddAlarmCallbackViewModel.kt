package com.github.tyngstast.borsdatavaluationalarmer.model

import com.github.tyngstast.borsdatavaluationalarmer.FlowAdapter

@Suppress("unused")
class AddAlarmCallbackViewModel(addAlarmModel: AddAlarmModel) : CallbackViewModel() {

    override val viewModel = BaseAddAlarmViewModel(addAlarmModel)

    val kpis: FlowAdapter<List<Item>> = viewModel.kpiStateFlow.asCallbacks()
    val instruments: FlowAdapter<List<Item>> = viewModel.instrumentStateFlow.asCallbacks()

    fun getInstruments(value: String) {
        viewModel.getInstruments(value)
    }

    fun getKpis(value: String) {
        viewModel.getKpis(value)
    }

    fun addAlarm(kpiValue: Double) {
        viewModel.addAlarm(kpiValue)
    }
}