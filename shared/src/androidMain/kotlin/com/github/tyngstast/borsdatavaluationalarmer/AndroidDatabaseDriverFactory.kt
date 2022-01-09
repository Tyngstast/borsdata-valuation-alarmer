package com.github.tyngstast.borsdatavaluationalarmer

import android.content.Context
import com.github.tyngstast.db.ValueAlarmerDb
import com.squareup.sqldelight.android.AndroidSqliteDriver
import com.squareup.sqldelight.db.SqlDriver

actual class DatabaseDriverFactory(private val context: Context) {
    actual fun createDriver(): SqlDriver {
        return AndroidSqliteDriver(ValueAlarmerDb.Schema, context, "ValueAlarmerDb")
    }
}