package com.github.tyngstast.borsdatavaluationalarmer.db

import com.github.tyngstast.db.Alarm
import com.squareup.sqldelight.db.SqlDriver
import com.squareup.sqldelight.runtime.coroutines.asFlow
import com.squareup.sqldelight.runtime.coroutines.mapToList
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn

class AlarmDao(
    sqlDriver: SqlDriver,
    private val backgroundDispatcher: CoroutineDispatcher
) : Dao(sqlDriver) {

    fun getAllEnabledAlarms(): List<Alarm> = dbQuery.selectAllEnabledAlarms().executeAsList()

    fun getAllAlarmsAsFlow(): Flow<List<Alarm>> =
        dbQuery
            .selectAllAlarms()
            .asFlow()
            .mapToList()
            .flowOn(backgroundDispatcher)

    fun insertAlarm(
        insId: Long,
        insName: String,
        yahooId: String,
        kpiId: Long,
        kpiName: String,
        kpiValue: Double,
        operation: String
    ) {
        dbQuery.insertAlarm(null, insId, insName, yahooId, kpiId, kpiName, kpiValue, operation)
    }

    fun updateDisableAlarm(id: Long, disable: Boolean) {
        dbQuery.updateDisabledAlarm(disable, id)
    }

    fun deleteAlarm(id: Long) {
        dbQuery.deleteAlarm(id)
    }
}