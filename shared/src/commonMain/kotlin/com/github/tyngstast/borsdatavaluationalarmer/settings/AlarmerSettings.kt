package com.github.tyngstast.borsdatavaluationalarmer.settings

import com.russhwolf.settings.Settings
import kotlinx.datetime.LocalDate

class AlarmerSettings(private val settings: Settings) {

    companion object {
        private const val DB_STOCK_DATA_TIMESTAMP_KEY = "DbStockDataTimestampKey"
        private const val LAST_RUN_PREFIX_KEY = "LAST_RUN_"
        private const val WORKER_FAILURE_COUNTER = "WorkerFailureCounter"
    }

    fun getLatestDataFetch() = settings.getLong(DB_STOCK_DATA_TIMESTAMP_KEY, 0)

    fun resetFailureCounter() {
        settings.putInt(WORKER_FAILURE_COUNTER, 0)
    }

    fun updateLatestDataFetch(timestamp: Long) {
        settings.putLong(DB_STOCK_DATA_TIMESTAMP_KEY, timestamp)
    }

    fun getLastRun(alarmId: Long): LocalDate? =
        settings.getStringOrNull(LAST_RUN_PREFIX_KEY + alarmId)
            ?.let { LocalDate.parse(it) }

    fun updateLastRun(id: Long, dateTime: String) {
        settings.putString(LAST_RUN_PREFIX_KEY + id, dateTime)
    }

    fun getFailureCount() = settings.getInt(WORKER_FAILURE_COUNTER, 0)

    fun incrementFailureCounter() {
        val failures: Int = settings.getInt(WORKER_FAILURE_COUNTER, 0)
        settings.putInt(WORKER_FAILURE_COUNTER, failures + 1)
    }
}