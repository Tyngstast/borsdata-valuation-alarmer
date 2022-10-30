package com.github.tyngstast.borsdatavaluationalarmer.android.ui.add

import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.runtime.Composable
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import com.github.tyngstast.borsdatavaluationalarmer.android.R
import com.github.tyngstast.borsdatavaluationalarmer.android.ui.common.InputField

@Composable
fun ValueInputField(
    value: String,
    isAbove: Boolean,
    toggleIsAbove: () -> Unit,
    onValueChange: (String) -> Unit = {},
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    focusRequester: FocusRequester = FocusRequester.Default,
) {

    InputField(
        value = value,
        label = stringResource(if (isAbove) R.string.kpi_above_threshold_label else R.string.kpi_below_threshold_label),
        onValueChange = onValueChange,
        keyboardActions = keyboardActions,
        keyboardOptions = keyboardOptions,
        focusRequester = focusRequester,
        trailingIcon = {
            IconButton(onClick = toggleIsAbove) {
                Icon(
                    if (isAbove) Icons.Default.ArrowUpward else Icons.Default.ArrowDownward,
                    stringResource(if (isAbove) R.string.kpi_above_threshold_icon_cd else R.string.kpi_below_threshold_icon_cd),
                    tint = Color.Gray
                )
            }
        }
    )
}
