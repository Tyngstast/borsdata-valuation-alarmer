package com.github.tyngstast.borsdatavaluationalarmer

import com.liftric.kvault.KVault

actual class KVaultFactory {
    actual fun store(): KVault = KVault(null, null)
}