package com.github.tyngstast.borsdatavaluationalarmer.client

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class InstrumentResponse(val instruments: List<InstrumentDto>)

@Serializable
data class InstrumentDto(
    val insId: Long,
    val name: String,
    val ticker: String,
    val yahoo: String
)

@Serializable
data class KpiResponse(val kpiHistoryMetadatas: List<KpiDto>)

@Serializable
data class KpiDto(
    val kpiId: Long,
    val nameSv: String,
    val format: String? = null
)

@Serializable
data class InsKpiResponse(
    val kpiId: Long,
    val group: String,
    val calculation: String,
    val value: KpiValue
)

@Serializable
data class KpiValue(
    // Instrument ID, echoed back
    val i: Long,
    // Number value when KPI is of that type
    val n: Double,
    // String value when KPI is of that type
    val s: String? = null
)

@Serializable
data class ReportsResponse(
    val instrument: String,
    val reports: List<Report>
)

@Serializable
data class Report(
    val year: Int,
    val period: Int,
    val revenues: Double,
    @SerialName("gross_Income")
    val grossIncome: Double,
    @SerialName("operating_Income")
    val operatingIncome: Double,
    @SerialName("earnings_Per_Share")
    val earningsPerShare: Double,
    @SerialName("number_Of_Shares")
    val numberOfShares: Double,
    val dividend: Double,
    @SerialName("net_Debt")
    val netDebt: Double,
    @SerialName("cash_Flow_From_Operating_Activities")
    val cashFlowFromOperatingActivities: Double,
    @SerialName("free_Cash_Flow")
    val freeCashFlow: Double,
    val currency: String
)
