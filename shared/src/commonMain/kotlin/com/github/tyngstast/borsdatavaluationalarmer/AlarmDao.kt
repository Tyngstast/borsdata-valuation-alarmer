package com.github.tyngstast.borsdatavaluationalarmer

import com.github.tyngstast.db.Alarm
import com.github.tyngstast.db.ValueAlarmerDb
import com.github.tyngstast.db.ValueAlarmerDbQueries
import com.squareup.sqldelight.db.SqlDriver
import com.squareup.sqldelight.runtime.coroutines.asFlow
import com.squareup.sqldelight.runtime.coroutines.mapToList
import kotlinx.coroutines.flow.Flow

class AlarmDao(sqlDriver: SqlDriver) {
    private val database = ValueAlarmerDb(sqlDriver)
    private val dbQuery: ValueAlarmerDbQueries = database.valueAlarmerDbQueries

    fun getAllAlarms(): List<Alarm> = dbQuery.selectAllAlarms().executeAsList()

    fun getAllAlarmsAsFlow(): Flow<List<Alarm>> =
        dbQuery
            .selectAllAlarms()
            .asFlow()
            .mapToList()

    fun getAlarm(id: Long): Alarm? = dbQuery.selectAlarm(id).executeAsOneOrNull()

    fun insertAlarm(
        insId: Long,
        insName: String,
        kpiId: Long,
        kpiName: String,
        kpiValue: Double,
        operation: String
    ) {
        dbQuery.insertAlarm(null, insId, insName, kpiId, kpiName, kpiValue, operation)
    }

    fun deleteAlarm(id: Long) {
        dbQuery.deleteAlarm(id)
    }
}