package com.github.tyngstast.borsdatavaluationalarmer.android.ui.login

import com.github.tyngstast.borsdatavaluationalarmer.model.ApiKeyState
import com.github.tyngstast.borsdatavaluationalarmer.model.BaseLoginViewModel
import com.github.tyngstast.borsdatavaluationalarmer.model.LoginModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn


class LoginViewModel(loginModel: LoginModel) : BaseLoginViewModel(loginModel) {

    val apiKeyState: StateFlow<ApiKeyState> = apiKeyStateFlow.stateIn(
        viewModelScope,
        SharingStarted.Eagerly,
        ApiKeyState.Empty
    )
}
