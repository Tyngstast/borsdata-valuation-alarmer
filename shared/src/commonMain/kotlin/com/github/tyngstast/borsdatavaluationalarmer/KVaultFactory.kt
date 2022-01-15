package com.github.tyngstast.borsdatavaluationalarmer

import com.liftric.kvault.KVault

expect class KVaultFactory {
    fun store(): KVault
}