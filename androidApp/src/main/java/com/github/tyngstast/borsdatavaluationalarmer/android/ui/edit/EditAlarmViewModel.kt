package com.github.tyngstast.borsdatavaluationalarmer.android.ui.edit

import androidx.lifecycle.ViewModel
import com.github.tyngstast.borsdatavaluationalarmer.db.AlarmDao
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class EditAlarmViewModel : ViewModel(), KoinComponent {

    private val alarmDao: AlarmDao by inject()

    fun getAlarm(id: Long) = alarmDao.getAlarm(id)

    fun editAlarm(id: Long, kpiValue: String) {
        alarmDao.updateKpiValue(id, kpiValue.toDouble())
    }
}
