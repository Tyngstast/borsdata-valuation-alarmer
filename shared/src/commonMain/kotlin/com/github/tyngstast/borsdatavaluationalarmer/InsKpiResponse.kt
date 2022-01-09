package com.github.tyngstast.borsdatavaluationalarmer

import kotlinx.serialization.Serializable

@Serializable
data class InsKpiResponse(
    val kpiId: Long,
    val group: String,
    val calculation: String,
    val value: Value
)

@Serializable
data class Value(
    // Instrument ID, echoed back
    val i: Long,
    // Number value when KPI is of that type
    val n: Double,
    // String value when KPI is of that type
    val s: String? = null
)