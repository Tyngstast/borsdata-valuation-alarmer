package com.github.tyngstast.borsdatavaluationalarmer.android.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.tyngstast.borsdatavaluationalarmer.Vault
import com.github.tyngstast.borsdatavaluationalarmer.client.BorsdataClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject


class LoginViewModel : ViewModel(), KoinComponent {
    private val vault: Vault by inject()
    private val borsdataClient: BorsdataClient by inject()

    private val _apiKeyState = MutableStateFlow<ApiKeyState>(ApiKeyState.Empty)
    val apiKeyState: StateFlow<ApiKeyState> = _apiKeyState.stateIn(
        viewModelScope,
        SharingStarted.Eagerly,
        ApiKeyState.Empty
    )

    init {
        vault.getApiKey()?.let {
            _apiKeyState.value = ApiKeyState.Success(it)
        }
    }

    fun clearKey() {
        vault.clearApiKey()
    }

    fun clearError() {
        _apiKeyState.value = ApiKeyState.Empty
    }

    fun verifyKey(key: String) = viewModelScope.launch {
        _apiKeyState.value = ApiKeyState.Loading
        try {
            val result = borsdataClient.verifyKey(key)
            if (result) {
                vault.setApiKey(key)
                _apiKeyState.value = ApiKeyState.Success(key)
            } else {
                _apiKeyState.value = ApiKeyState.Error(ErrorCode.UNAUTHORIZED)
            }
        } catch (e: Exception) {
            _apiKeyState.value = ApiKeyState.Error(ErrorCode.SERVICE_ERROR)
        }
    }

    sealed class ApiKeyState {
        data class Success(val apiKey: String): ApiKeyState()
        data class Error(val errorCode: ErrorCode): ApiKeyState()
        object Loading: ApiKeyState()
        object Empty: ApiKeyState()
    }

    enum class ErrorCode(val value: String) {
        UNAUTHORIZED("Felaktig API-nyckel"),
        SERVICE_ERROR("Oväntat fel vid anrop mot Börsdata")
    }
}
