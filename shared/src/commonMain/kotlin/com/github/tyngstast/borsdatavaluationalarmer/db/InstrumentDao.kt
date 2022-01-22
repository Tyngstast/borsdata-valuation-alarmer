package com.github.tyngstast.borsdatavaluationalarmer.db

import com.github.tyngstast.borsdatavaluationalarmer.InstrumentDto
import com.squareup.sqldelight.db.SqlDriver
import kotlinx.coroutines.CoroutineDispatcher

class InstrumentDao(
    sqlDriver: SqlDriver,
    private val backgroundDispatcher: CoroutineDispatcher
) : Dao(sqlDriver) {

    suspend fun resetInstruments(instruments: List<InstrumentDto>) {
        dbQuery.transactionWithContext(backgroundDispatcher) {
            dbQuery.deleteAllInstruments()
            instruments.forEach {
                dbQuery.insertInstrument(
                    id = null,
                    insId = it.insId,
                    yahooId = it.yahoo,
                    name = it.name,
                    ticker = it.ticker
                )
            }
        }
    }
}