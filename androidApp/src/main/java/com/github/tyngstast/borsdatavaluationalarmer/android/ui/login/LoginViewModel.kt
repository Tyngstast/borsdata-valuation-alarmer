package com.github.tyngstast.borsdatavaluationalarmer.android.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.tyngstast.borsdatavaluationalarmer.BorsdataApi
import com.github.tyngstast.borsdatavaluationalarmer.Vault
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject


class LoginViewModel : ViewModel(), KoinComponent {
    private val vault: Vault by inject()
    private val borsdataApi: BorsdataApi by inject()

    private val _apiKeyState = MutableStateFlow(ApiKeyState(""))
    val apiKeyState: StateFlow<ApiKeyState> = _apiKeyState

    init {
        vault.getApiKey()?.let {
            _apiKeyState.value = ApiKeyState(it)
        }
    }

    fun clearError() {
        _apiKeyState.value = _apiKeyState.value.copy(error = null)
    }

    fun verifyKey(key: String) = viewModelScope.launch {
        _apiKeyState.value = ApiKeyState("", loading = true)
        try {
            val result = borsdataApi.verifyKey(key)
            if (result) {
                vault.setApiKey(key)
                _apiKeyState.value = ApiKeyState(key, false, null)
            } else {
                _apiKeyState.value = ApiKeyState("", false, ErrorCode.UNAUTHORIZED)
            }
        } catch (e: Exception) {
            _apiKeyState.value = ApiKeyState("", false, ErrorCode.SERVICE_ERROR)
        }
    }
}

enum class ErrorCode(val value: String) {
    UNAUTHORIZED("Felaktig API-nyckel"),
    SERVICE_ERROR("Oväntat fel vid anrop mot Börsdata")
}

data class ApiKeyState(
    val apiKey: String,
    val loading: Boolean = false,
    val error: ErrorCode? = null
)
