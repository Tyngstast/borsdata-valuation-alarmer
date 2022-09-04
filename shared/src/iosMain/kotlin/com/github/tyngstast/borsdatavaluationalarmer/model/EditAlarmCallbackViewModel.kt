package com.github.tyngstast.borsdatavaluationalarmer.model

@Suppress("unused")
class EditAlarmCallbackViewModel(editAlarmModel: EditAlarmModel) : CallbackViewModel() {
    override val viewModel = BaseEditAlarmViewModel(editAlarmModel)

    fun getAlarm(id: Long) = viewModel.getAlarm(id)

    fun editAlarm(id: Long, kpiValue: Double) {
        viewModel.editAlarm(id, kpiValue)
    }
}