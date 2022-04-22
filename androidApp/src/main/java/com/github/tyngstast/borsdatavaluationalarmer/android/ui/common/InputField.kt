package com.github.tyngstast.borsdatavaluationalarmer.android.ui.common

import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.ZeroCornerSize
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.LocalTextStyle
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.TextFieldDefaults.indicatorLine
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.FocusState
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.takeOrElse
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterialApi::class)
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
    val interactionSource = remember { MutableInteractionSource() }
    val colors = if (MaterialTheme.colors.isLight) TextFieldDefaults.textFieldColors(backgroundColor = Color.White) else TextFieldDefaults.textFieldColors()
    val shape = MaterialTheme.shapes.small.copy(bottomEnd = ZeroCornerSize, bottomStart = ZeroCornerSize)
    val enabled = !disabled
    val textStyle = LocalTextStyle.current
    val textColor = textStyle.color.takeOrElse { colors.textColor(enabled).value }
    val mergedTextStyle = textStyle.merge(TextStyle(color = textColor, fontSize = 18.sp))

    // TODO: keyboard does not seem to go away when navigating back
    @OptIn(ExperimentalMaterialApi::class)
    BasicTextField(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 4.dp)
            .onFocusChanged(onFocusChange)
            .focusRequester(focusRequester)
            .background(colors.backgroundColor(enabled).value, shape)
            .indicatorLine(enabled, false, interactionSource, colors)
            .defaultMinSize(
                minWidth = TextFieldDefaults.MinWidth,
                minHeight = TextFieldDefaults.MinHeight
            ),
        value = value,
        singleLine = true,
        enabled = enabled,
        textStyle = mergedTextStyle,
        onValueChange = onValueChange,
        keyboardActions = keyboardActions,
        keyboardOptions = keyboardOptions,
        interactionSource = interactionSource,
        decorationBox = @Composable { innerTextField ->
            // places leading icon, text field with label and placeholder, trailing icon
            TextFieldDefaults.TextFieldDecorationBox(
                value = value,
                enabled = enabled,
                innerTextField = innerTextField,
                label = { Text(label, fontSize = 16.sp) },
                singleLine = true,
                visualTransformation = VisualTransformation.None,
                interactionSource = interactionSource,
                colors = colors,
                contentPadding = PaddingValues(8.dp)
            )
        }
    )
}

