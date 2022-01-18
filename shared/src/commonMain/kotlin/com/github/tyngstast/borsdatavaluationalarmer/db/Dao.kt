package com.github.tyngstast.borsdatavaluationalarmer.db

import com.github.tyngstast.db.ValueAlarmerDb
import com.github.tyngstast.db.ValueAlarmerDbQueries
import com.squareup.sqldelight.db.SqlDriver

abstract class Dao(sqlDriver: SqlDriver) {
    private val database = ValueAlarmerDb(sqlDriver)
    internal val dbQuery: ValueAlarmerDbQueries = database.valueAlarmerDbQueries
}