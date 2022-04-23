package com.github.tyngstast.borsdatavaluationalarmer.android.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.tyngstast.borsdatavaluationalarmer.model.LoginModel
import com.github.tyngstast.borsdatavaluationalarmer.model.LoginModel.ApiKeyState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch


class LoginViewModel(private val loginModel: LoginModel) : ViewModel() {

    private val _apiKeyState = MutableStateFlow<ApiKeyState>(ApiKeyState.Empty)
    val apiKeyState: StateFlow<ApiKeyState> = _apiKeyState.stateIn(
        viewModelScope,
        SharingStarted.Eagerly,
        ApiKeyState.Empty
    )

    init {
        loginModel.getApiKey()?.let {
            _apiKeyState.value = ApiKeyState.Success(it)
        }
    }

    fun clearKey() {
        loginModel.clearApiKey()
    }

    fun clearError() {
        _apiKeyState.value = ApiKeyState.Empty
    }

    fun verifyKey(key: String) = viewModelScope.launch {
        loginModel.verifyKey(key).collect {
            _apiKeyState.value = it
        }
    }
}
