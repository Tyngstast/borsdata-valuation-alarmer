package com.github.tyngstast.borsdatavaluationalarmer

import android.content.Context
import com.liftric.kvault.KVault

actual class KVaultFactory(private val context: Context) {
    actual fun store(): KVault {
        return KVault(context)
    }
}