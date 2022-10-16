package com.github.tyngstast.borsdatavaluationalarmer

actual val isDebug = BuildConfig.DEBUG

actual fun Double.format(format: String): String {
    return String.format(format, this)
}