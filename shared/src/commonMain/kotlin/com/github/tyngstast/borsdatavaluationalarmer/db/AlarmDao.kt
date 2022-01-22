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
    fun getAllAlarms(): List<Alarm> = dbQuery.selectAllAlarms().executeAsList()

    fun getAllAlarmsAsFlow(): Flow<List<Alarm>> =
        dbQuery
            .selectAllAlarms()
            .asFlow()
            .mapToList()
            .flowOn(backgroundDispatcher)

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