package com.github.tyngstast.borsdatavaluationalarmer.util

import com.github.tyngstast.db.Alarm

fun Alarm.evaluate(kpiValue: Double): Boolean =
    if (this.operation == "gt") {
        kpiValue.compareTo(this.kpiValue) > 0
    } else { // lt is only other option for now
        kpiValue.compareTo(this.kpiValue) < 0
    }

fun String.isDouble(): Boolean = try {
    this.replace(",", ".").toDouble()
    true
} catch (e: Throwable) {
    false
}