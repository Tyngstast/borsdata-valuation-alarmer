package com.github.tyngstast.borsdatavaluationalarmer

import com.github.tyngstast.db.Alarm
import com.github.tyngstast.db.ValueAlarmerDb
import com.github.tyngstast.db.ValueAlarmerDbQueries

class Dao(databaseDriverFactory: DatabaseDriverFactory) {
    private val database = ValueAlarmerDb(databaseDriverFactory.createDriver())
    private val dbQuery: ValueAlarmerDbQueries = database.valueAlarmerDbQueries

    fun getAllAlarms(): List<Alarm> {
        return dbQuery.selectAllAlarms().executeAsList()
    }

    fun insertAlarm(insId: Long, insName: String, kpiId: Long, value: Double) {
        dbQuery.insertAlarm(null, insId, insName, kpiId, value)
    }
}