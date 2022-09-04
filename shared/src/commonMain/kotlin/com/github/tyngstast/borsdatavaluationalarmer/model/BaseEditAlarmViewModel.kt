package com.github.tyngstast.borsdatavaluationalarmer.model

open class BaseEditAlarmViewModel(private val editAlarmModel: EditAlarmModel) : ViewModel() {

    fun getAlarm(id: Long) = editAlarmModel.getAlarm(id)

    fun editAlarm(id: Long, kpiValue: Double) {
        editAlarmModel.editAlarmValue(id, kpiValue)
    }
}
