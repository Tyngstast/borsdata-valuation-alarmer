package com.github.tyngstast.borsdatavaluationalarmer.db

import com.github.tyngstast.borsdatavaluationalarmer.client.KpiDto
import com.github.tyngstast.borsdatavaluationalarmer.model.KpiType
import com.github.tyngstast.db.Kpi
import com.squareup.sqldelight.db.SqlDriver
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

class KpiDao(
    sqlDriver: SqlDriver,
    private val backgroundDispatcher: CoroutineDispatcher
) : Dao(sqlDriver) {

    suspend fun getKpis(name: String): List<Kpi> {
        return withContext(backgroundDispatcher) {
            dbQuery.selectKpis(name).executeAsList()
        }
    }

    suspend fun getKpisEn(name: String): List<Kpi> {
        return withContext(backgroundDispatcher) {
            dbQuery.selectKpisEn(name).executeAsList()
        }
    }

    suspend fun resetKpis(kpis: List<KpiDto>) {
        dbQuery.transactionWithContext(backgroundDispatcher) {
            dbQuery.deleteAllKpis()
            kpis.forEach {
                dbQuery.insertKpi(
                    id = null,
                    kpidId = it.kpiId,
                    name = it.nameSv,
                    nameEn = it.nameEn,
                    format = it.format,
                    // We only insert TA types manually for now
                    type = KpiType.FA.name
                )
            }
        }
    }
}