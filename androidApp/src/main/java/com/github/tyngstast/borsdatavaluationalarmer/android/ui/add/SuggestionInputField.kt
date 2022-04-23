package com.github.tyngstast.borsdatavaluationalarmer.android.ui.add

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import com.github.tyngstast.borsdatavaluationalarmer.android.ui.common.InputField
import com.github.tyngstast.borsdatavaluationalarmer.model.Item
import com.github.tyngstast.borsdatavaluationalarmer.model.KpiItem

@Composable
fun SuggestionInputField(
    value: String,
    label: String,
    disabled: Boolean = false,
    onValueChange: (String) -> Unit,
    keyboardActions: KeyboardActions,
    keyboardOptions: KeyboardOptions,
    focusRequester: FocusRequester,
    items: List<Item> = listOf(),
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
        focusRequester = focusRequester,
        disabled = disabled
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
                        .padding(horizontal = 16.dp)
                        .clickable {
                            onValueChange(item.name)
                            showSuggestions = false
                            focusManager.moveFocus(FocusDirection.Down)
                        }
                ) {
                    Row(
                        Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = item.name, modifier = Modifier.padding(vertical = 8.dp))
                        if (item is KpiItem && item.fluent) {
                            Icon(
                                Icons.Default.Bolt,
                                contentDescription = "Fluent"
                            )
                        }
                    }
                }
            }
        }
    }
}
