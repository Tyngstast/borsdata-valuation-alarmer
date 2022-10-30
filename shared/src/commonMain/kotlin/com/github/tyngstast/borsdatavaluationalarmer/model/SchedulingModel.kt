package com.github.tyngstast.borsdatavaluationalarmer.model

import com.github.tyngstast.borsdatavaluationalarmer.settings.AlarmerSettings
import com.github.tyngstast.borsdatavaluationalarmer.settings.Vault

class SchedulingModel(
    private val alarmerSettings: AlarmerSettings,
    private val vault: Vault,
) {
    companion object {
        private const val FAILURE_THRESHOLD: Int = 3
    }

    fun scheduleNext(): Boolean {
        val key = vault.getApiKey()
        val failures = alarmerSettings.getFailureCount()
        return !key.isNullOrBlank() && failures < FAILURE_THRESHOLD
    }
}
