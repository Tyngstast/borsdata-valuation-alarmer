package com.github.tyngstast.borsdatavaluationalarmer.db

import com.github.tyngstast.borsdatavaluationalarmer.KpiDto
import com.squareup.sqldelight.db.SqlDriver

class KpiDao(sqlDriver: SqlDriver) : Dao(sqlDriver) {

    fun resetKpis(kpis: List<KpiDto>) {
        dbQuery.transaction {
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