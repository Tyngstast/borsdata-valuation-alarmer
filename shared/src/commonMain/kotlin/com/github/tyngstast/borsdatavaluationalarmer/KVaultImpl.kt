package com.github.tyngstast.borsdatavaluationalarmer

class KVaultImpl(kVaultFactory: KVaultFactory) {

    companion object {
        private const val AUTH_KEY = "AUTH_KEY";
    }

    private val store = kVaultFactory.store()

    fun setApiKey(key: String) {
        store.set(AUTH_KEY, key)
    }

    fun getApiKey(): String? {
        return store.string(AUTH_KEY)
    }
}