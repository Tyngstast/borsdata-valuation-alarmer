package com.github.tyngstast.borsdatavaluationalarmer.db

import com.github.tyngstast.borsdatavaluationalarmer.KpiDto
import com.squareup.sqldelight.db.SqlDriver
import kotlinx.coroutines.CoroutineDispatcher

class KpiDao(
    sqlDriver: SqlDriver,
    private val backgroundDispatcher: CoroutineDispatcher
) : Dao(sqlDriver) {

    suspend fun resetKpis(kpis: List<KpiDto>) {
        dbQuery.transactionWithContext(backgroundDispatcher) {
            dbQuery.deleteAllKpis()
            kpis.forEach {
                dbQuery.insertKpi(
                    id = null,
                    kpidId = it.kpiId,
                    name = it.nameSv,
                    format = it.format
                )
            }
        }
    }
}