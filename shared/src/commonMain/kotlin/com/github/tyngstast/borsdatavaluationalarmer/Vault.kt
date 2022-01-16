package com.github.tyngstast.borsdatavaluationalarmer

import com.liftric.kvault.KVault

class Vault(kVault: KVault) {

    companion object {
        private const val AUTH_KEY = "AUTH_KEY";
    }

    private val vault = kVault

    fun setApiKey(key: String) {
        vault.set(AUTH_KEY, key)
    }

    fun getApiKey(): String? {
        return vault.string(AUTH_KEY)
    }
}