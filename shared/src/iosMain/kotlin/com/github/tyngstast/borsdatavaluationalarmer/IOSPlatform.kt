package com.github.tyngstast.borsdatavaluationalarmer

import platform.Foundation.NSString
import platform.Foundation.stringWithFormat

actual val isDebug = Platform.isDebugBinary

actual fun Double.format(format: String): String {
    return NSString.stringWithFormat(format, this)
}

