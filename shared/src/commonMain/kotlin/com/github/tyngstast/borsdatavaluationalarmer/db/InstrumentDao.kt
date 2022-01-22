package com.github.tyngstast.borsdatavaluationalarmer.db

import com.github.tyngstast.borsdatavaluationalarmer.InstrumentDto
import com.github.tyngstast.db.Instrument
import com.squareup.sqldelight.db.SqlDriver
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

class InstrumentDao(
    sqlDriver: SqlDriver,
    private val backgroundDispatcher: CoroutineDispatcher
) : Dao(sqlDriver) {

    suspend fun getInstruments(name: String): List<Instrument> {
        return withContext(backgroundDispatcher) {
            dbQuery.selectInstruments(name).executeAsList()
        }
    }

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