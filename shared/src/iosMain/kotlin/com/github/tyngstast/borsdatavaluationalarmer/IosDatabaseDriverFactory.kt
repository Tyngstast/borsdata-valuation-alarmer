package com.github.tyngstast.borsdatavaluationalarmer

import com.github.tyngstast.db.ValueAlarmerDb
import com.squareup.sqldelight.db.SqlDriver
import com.squareup.sqldelight.drivers.native.NativeSqliteDriver

actual class DatabaseDriverFactory {
    actual fun createDriver(): SqlDriver {
        return NativeSqliteDriver(ValueAlarmerDb.Schema, "ValueAlarmerDb")
    }
}