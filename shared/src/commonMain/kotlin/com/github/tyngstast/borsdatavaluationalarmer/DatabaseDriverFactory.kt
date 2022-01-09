package com.github.tyngstast.borsdatavaluationalarmer

import com.squareup.sqldelight.db.SqlDriver

expect class DatabaseDriverFactory {
    fun createDriver(): SqlDriver
}