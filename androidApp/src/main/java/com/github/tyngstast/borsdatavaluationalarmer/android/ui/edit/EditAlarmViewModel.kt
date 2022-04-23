package com.github.tyngstast.borsdatavaluationalarmer.android.ui.edit

import androidx.lifecycle.ViewModel
import com.github.tyngstast.borsdatavaluationalarmer.model.EditAlarmModel

class EditAlarmViewModel(private val editAlarmModel: EditAlarmModel) : ViewModel() {

    fun getAlarm(id: Long) = editAlarmModel.getAlarm(id)

    fun editAlarm(id: Long, kpiValue: String) {
        editAlarmModel.editAlarmValue(id, kpiValue)
    }
}
