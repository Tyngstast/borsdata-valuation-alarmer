package com.github.tyngstast.borsdatavaluationalarmer.model

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch


open class LoginViewModel(private val loginModel: LoginModel) : ViewModel() {

    private val _apiKeyStateFlow = MutableStateFlow<ApiKeyState>(ApiKeyState.Empty)
    val apiKeyStateFlow: StateFlow<ApiKeyState> = _apiKeyStateFlow

    init {
        loginModel.getApiKey()?.let {
            _apiKeyStateFlow.value = ApiKeyState.Success
        }
    }

    fun clearKey() {
        loginModel.clearApiKey()
    }

    fun clearError() {
        _apiKeyStateFlow.value = ApiKeyState.Empty
    }

    fun verifyKey(key: String) = viewModelScope.launch {
        loginModel.verifyKey(key).collect {
            _apiKeyStateFlow.value = it
        }
    }
}
