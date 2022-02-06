package com.github.tyngstast.borsdatavaluationalarmer

actual class Platform actual constructor() {
    actual val platform: String = "Android ${android.os.Build.VERSION.SDK_INT}"
}
