package com.github.tyngstast.borsdatavaluationalarmer

import com.github.tyngstast.db.Alarm

val operationsMap = mapOf(
    "lte" to { v1: Double, v2: Double -> v1.compareTo(v2) <= 0 },
    "gte" to { v1: Double, v2: Double -> v1.compareTo(v2) >= 0 }
)

fun Alarm.evaluate(kpiValue: Double): Boolean =
    operationsMap.getValue(this.operation).invoke(kpiValue, this.kpiValue)

fun String.isDouble(): Boolean = try {
    this.replace(",", ".").toDouble()
    true
} catch (e: Throwable) {
    false
}