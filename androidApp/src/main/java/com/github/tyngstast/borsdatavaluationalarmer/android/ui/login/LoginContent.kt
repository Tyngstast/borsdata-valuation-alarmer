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
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Job

@OptIn(ExperimentalComposeUiApi::class)
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
            isError = state.error != null,
            trailingIcon = {
                when {
                    state.loading -> CircularProgressIndicator(modifier = Modifier.scale(0.5F))
                    state.error != null -> Icon(
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
            enabled = !state.loading,
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            keyboardActions = KeyboardActions(onDone = { evaluateKey() }),
            keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done)
        )
        if (state.error != null) {
            Text(
                text = state.error.value,
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
            enabled = apiKey.length > 20 && !state.loading,
            onClick = { evaluateKey() }
        ) {
            Text("VERIFIERA")
        }
    }
}