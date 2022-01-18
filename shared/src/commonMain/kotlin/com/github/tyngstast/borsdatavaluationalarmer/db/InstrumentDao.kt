package com.github.tyngstast.borsdatavaluationalarmer.db

import com.github.tyngstast.borsdatavaluationalarmer.InstrumentDto
import com.squareup.sqldelight.db.SqlDriver

class InstrumentDao(sqlDriver: SqlDriver) : Dao(sqlDriver) {

    fun resetInstruments(instruments: List<InstrumentDto>) {
        dbQuery.transaction {
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