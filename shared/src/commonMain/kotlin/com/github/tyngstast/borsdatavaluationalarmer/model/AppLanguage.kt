package com.github.tyngstast.borsdatavaluationalarmer.model

enum class AppLanguage {
    SV,
    EN;

    companion object {
        fun fromString(value: String): AppLanguage = if (value.equals("sv", ignoreCase = true)) SV else EN;
    }
}
