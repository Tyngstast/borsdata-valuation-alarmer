package com.github.tyngstast.borsdatavaluationalarmer.android.ui.add

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.FocusState
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun AddAlarmField(
    value: String,
    label: String,
    onValueChange: (String) -> Unit,
    items: List<Item> = listOf(),
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    focusRequester: FocusRequester = FocusRequester.Default
) {
    val focusManager = LocalFocusManager.current

    var showSuggestions: Boolean by remember { mutableStateOf(true) }

    InputField(
        value = value,
        label = label,
        onValueChange = { v: String ->
            onValueChange(v)
            showSuggestions = true
        },
        onFocusChange = { showSuggestions = false },
        keyboardActions = keyboardActions,
        keyboardOptions = keyboardOptions,
        focusRequester = focusRequester
    )
    AnimatedVisibility(visible = showSuggestions) {
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            items(items) { item ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            onValueChange(item.name)
                            showSuggestions = false
                            focusManager.moveFocus(FocusDirection.Down)
                        }
                ) {
                    Text(text = item.name, modifier = Modifier.padding(8.dp))
                }
            }
        }
    }
}

@Composable
private fun InputField(
    value: String,
    label: String,
    onValueChange: (String) -> Unit,
    onFocusChange: (FocusState) -> Unit,
    keyboardActions: KeyboardActions,
    keyboardOptions: KeyboardOptions,
    focusRequester: FocusRequester
) = TextField(
    value = value,
    singleLine = true,
    colors = TextFieldDefaults.textFieldColors(backgroundColor = Color.White),
    modifier = Modifier
        .fillMaxWidth()
        .onFocusChanged(onFocusChange)
        .focusRequester(focusRequester),
    label = { Text(label, fontSize = 16.sp) },
    onValueChange = onValueChange,
    keyboardActions = keyboardActions,
    keyboardOptions = keyboardOptions
)


