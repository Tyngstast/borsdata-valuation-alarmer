package com.github.tyngstast.borsdatavaluationalarmer

enum class FluentKpi(val value: String, val denominatorId: Long) {
    P_E("P/E", 6), // EPS
    EV_EBIT("EV/EBIT", 55), // EBIT
    EV_EBITDA("EV/EBITDA", 54), // EBITDA
    EV_FCF("EV/FCF", 63), // FCF
    EV_OP("EV/OP", 62), // OCF
    EV_S("EV/S", 53), // Sales
    EV_E("EV/E", 56); // Earnings

    companion object {
        val stringValues = values().map { it.value }
    }
}