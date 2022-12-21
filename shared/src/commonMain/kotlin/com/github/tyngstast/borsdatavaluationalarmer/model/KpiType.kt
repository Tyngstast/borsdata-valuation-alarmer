package com.github.tyngstast.borsdatavaluationalarmer.model

enum class KpiType {
    FA,
    TA;

    companion object {
        // Default to FA if value is missing
        fun fromString(value: String?): KpiType = if (value?.equals("TA", ignoreCase = true) == true) TA else FA;
    }
}