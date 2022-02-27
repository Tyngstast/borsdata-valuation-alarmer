package com.github.tyngstast.borsdatavaluationalarmer.android.ui.common

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.FocusState
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.sp

@Composable
fun InputField(
    value: String,
    label: String,
    onValueChange: (String) -> Unit = {},
    onFocusChange: (FocusState) -> Unit = {},
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    focusRequester: FocusRequester = FocusRequester.Default,
    disabled: Boolean = false,
) {
    TextField(
        value = value,
        singleLine = true,
        colors = if (MaterialTheme.colors.isLight) TextFieldDefaults.textFieldColors(backgroundColor = Color.White) else TextFieldDefaults.textFieldColors(),
        enabled = !disabled,
        modifier = Modifier
            .fillMaxWidth()
            .onFocusChanged(onFocusChange)
            .focusRequester(focusRequester),
        label = { Text(label, fontSize = 16.sp) },
        onValueChange = onValueChange,
        keyboardActions = keyboardActions,
        keyboardOptions = keyboardOptions
    )
}

