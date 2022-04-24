package com.github.tyngstast.borsdatavaluationalarmer.model

import com.github.tyngstast.borsdatavaluationalarmer.client.BorsdataClient
import com.github.tyngstast.borsdatavaluationalarmer.settings.Vault
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class LoginModel(
    private val vault: Vault,
    private val borsdataClient: BorsdataClient
) {

    fun getApiKey(): String? {
        return vault.getApiKey()
    }

    fun clearApiKey() {
        vault.clearApiKey()
    }

    suspend fun verifyKey(key: String): Flow<ApiKeyState> = flow {
        emit(ApiKeyState.Loading)
        try {
            val result = borsdataClient.verifyKey(key)
            if (result) {
                vault.setApiKey(key)
                emit(ApiKeyState.Success)
            } else {
                emit(ApiKeyState.Error(ErrorCode.UNAUTHORIZED))
            }
        } catch (e: Exception) {
            emit(ApiKeyState.Error(ErrorCode.SERVICE_ERROR))
        }
    }
}

sealed class ApiKeyState {
    data class Error(val errorCode: ErrorCode) : ApiKeyState()
    object Success : ApiKeyState()
    object Loading : ApiKeyState()
    object Empty : ApiKeyState()
}

enum class ErrorCode(val resourceId: String) {
    UNAUTHORIZED("login_unauthorized"),
    SERVICE_ERROR("login_service_error")
}
