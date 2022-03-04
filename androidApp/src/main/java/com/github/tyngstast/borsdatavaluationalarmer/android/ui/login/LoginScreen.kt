package com.github.tyngstast.borsdatavaluationalarmer.android.ui.login

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
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
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.github.tyngstast.borsdatavaluationalarmer.android.ui.login.LoginViewModel.ApiKeyState
import com.github.tyngstast.borsdatavaluationalarmer.android.ui.login.LoginViewModel.ApiKeyState.Error
import com.github.tyngstast.borsdatavaluationalarmer.android.ui.login.LoginViewModel.ApiKeyState.Loading
import com.github.tyngstast.borsdatavaluationalarmer.android.ui.login.LoginViewModel.ApiKeyState.Success
import kotlinx.coroutines.Job
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

    val state by viewModel.apiKeyState.collectAsState()

    if (state !is Loading && state is Success && !successCalled) {
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
            TopAppBar(
                title = { Text("Kräver Börsdata Pro") },
                backgroundColor = MaterialTheme.colors.primaryVariant
            )
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

@Composable
fun LoginContent(
    apiKey: String,
    onChange: (String) -> Unit,
    state: ApiKeyState,
    apiKeyVisibility: Boolean,
    toggleVisibility: () -> Unit,
    evaluateKey: () -> Job
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
            isError = state is Error,
            trailingIcon = {
                when {
                    state is Loading -> CircularProgressIndicator(modifier = Modifier.scale(0.5F))
                    state is Error -> Icon(
                        Icons.Default.Info,
                        "Fel",
                        tint = MaterialTheme.colors.error
                    )
                    else -> IconButton(onClick = toggleVisibility) {
                        Icon(
                            if (apiKeyVisibility) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                            "Toggla API-nyckel synlighet"
                        )
                    }
                }
            },
            label = { Text("API-nyckel") },
            enabled = state !is Loading,
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            keyboardActions = KeyboardActions(onDone = { evaluateKey() }),
            keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done)
        )
        if (state is Error) {
            Text(
                text = state.errorCode.value,
                color = MaterialTheme.colors.error,
                style = MaterialTheme.typography.caption,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
        Button(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 0.dp, vertical = 12.dp),
            contentPadding = PaddingValues(all = 12.dp),
            enabled = apiKey.length > 20 && state !is Loading,
            onClick = { evaluateKey() }
        ) {
            Text("VERIFIERA")
        }
    }
}
