package com.github.tyngstast.borsdatavaluationalarmer.model

import com.github.tyngstast.borsdatavaluationalarmer.db.AlarmDao

class EditAlarmModel(private val alarmDao: AlarmDao) {

    fun getAlarm(id: Long) = alarmDao.getAlarm(id)

    fun editAlarmValue(id: Long, kpiValue: Double) {
        alarmDao.updateKpiValue(id, kpiValue)
    }
}
