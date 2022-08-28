package com.github.tyngstast.borsdatavaluationalarmer.model

@Suppress("unused")
class LoginCallbackViewModel(loginModel: LoginModel) : CallbackViewModel() {

    override val viewModel = BaseLoginViewModel(loginModel)

    val apiKeyState = viewModel.apiKeyStateFlow.asCallbacks()

    fun clearKey() {
        viewModel.clearKey()
    }

    fun clearError() {
        viewModel.clearError()
    }

    fun verifyKey(key: String) {
        viewModel.verifyKey(key)
    }
}