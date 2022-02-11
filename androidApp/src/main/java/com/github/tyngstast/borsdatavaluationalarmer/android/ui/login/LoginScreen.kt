package com.github.tyngstast.borsdatavaluationalarmer.android.ui.login

import android.annotation.SuppressLint
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.lifecycle.flowWithLifecycle
import kotlinx.coroutines.flow.Flow
import org.koin.androidx.compose.getViewModel

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun LoginScreen(
    onSuccess: () -> Unit,
    viewModel: LoginViewModel = getViewModel()
) {
    val keyboardController = LocalSoftwareKeyboardController.current

    var apiKey: String by remember { mutableStateOf("") }
    var apiKeyVisibility: Boolean by remember { mutableStateOf(false) }
    var successCalled: Boolean by remember { mutableStateOf(false) }

    val lifecycleOwner = LocalLifecycleOwner.current
    val lifecycleAwareAlarmsFlow: Flow<ApiKeyState> =
        remember(viewModel.apiKeyState, lifecycleOwner) {
            viewModel.apiKeyState.flowWithLifecycle(lifecycleOwner.lifecycle)
        }

    @SuppressLint("StateFlowValueCalledInComposition")
    val state by lifecycleAwareAlarmsFlow.collectAsState(viewModel.apiKeyState.value)

    if (!state.loading && state.apiKey.isNotBlank() && !successCalled) {
        // Safe guard for multiple pop backs. Find a better way to do this
        successCalled = true
        keyboardController?.hide()
        onSuccess()
    }

    val evaluateKey = {
        viewModel.clearError()
        viewModel.verifyKey(apiKey)
    }

    val onChange: (String) -> Unit = { input: String ->
        viewModel.clearError()
        // Multiple character increase from single event -> paste
        if (input.length - 1 > apiKey.length) {
            apiKey = input
            evaluateKey()
        } else {
            apiKey = input
        }
    }

    val toggleVisibility = {
        apiKeyVisibility = !apiKeyVisibility
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Kräver Börsdata Pro") })
        },
    ) {
        LoginContent(
            apiKey = apiKey,
            onChange = onChange,
            state = state,
            apiKeyVisibility = apiKeyVisibility,
            toggleVisibility = toggleVisibility,
            evaluateKey = evaluateKey
        )
    }
}

