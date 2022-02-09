package com.github.tyngstast.borsdatavaluationalarmer.android.ui.login

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.flowWithLifecycle
import kotlinx.coroutines.flow.Flow
import org.koin.androidx.compose.getViewModel

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun Login(
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

    fun evaluateKey() {
        viewModel.clearError()
        viewModel.verifyKey(apiKey)
    }

    val onChange = { input: String ->
        viewModel.clearError()
        apiKey = input
        if (apiKey.length >= 32) {
            evaluateKey()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Kräver Börsdata Pro") })
        },
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            OutlinedTextField(
                value = apiKey,
                onValueChange = onChange,
                visualTransformation = if (apiKeyVisibility) VisualTransformation.None else PasswordVisualTransformation(),
                isError = state.error != null,
                trailingIcon = {
                    when {
                        state.loading -> CircularProgressIndicator(modifier = Modifier.scale(0.5F))
                        state.error != null -> Icon(
                            Icons.Default.Info,
                            "Fel",
                            tint = MaterialTheme.colors.error
                        )
                        else -> IconButton(onClick = { apiKeyVisibility = !apiKeyVisibility }) {
                            Icon(
                                if (apiKeyVisibility) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                "Toggla API-nyckel synlighet"
                            )
                        }
                    }
                },
                label = { Text("API-nyckel") },
                enabled = !state.loading,
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                keyboardActions = KeyboardActions(onDone = { evaluateKey() }),
                keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done)
            )
            if (state.error != null) {
                Text(
                    text = state.error!!.value,
                    color = MaterialTheme.colors.error,
                    style = MaterialTheme.typography.caption,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }
    }
}
