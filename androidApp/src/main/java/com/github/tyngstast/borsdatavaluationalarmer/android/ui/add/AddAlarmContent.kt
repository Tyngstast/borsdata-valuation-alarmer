package com.github.tyngstast.borsdatavaluationalarmer.android.ui.add

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import com.github.tyngstast.borsdatavaluationalarmer.android.ui.common.InputField
import com.github.tyngstast.borsdatavaluationalarmer.android.ui.theme.AppTheme

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun AddAlarmContent(
    insName: String,
    kpiName: String,
    kpiValue: String,
    instruments: List<Item>,
    kpis: List<Item>,
    setInsName: (String) -> Unit,
    setKpiName: (String) -> Unit,
    setKpiValue: (String) -> Unit,
    addAlarm: () -> Unit
) {
    val (insNameFr, kpiNameFr, kpiValueFr) = FocusRequester.createRefs()

    LaunchedEffect(Unit) {
        insNameFr.requestFocus()
    }

    Column(
        Modifier
            .fillMaxWidth()
            .fillMaxHeight()
    ) {
        SuggestionInputField(
            value = insName,
            label = "Bolag",
            onValueChange = setInsName,
            items = instruments,
            keyboardActions = KeyboardActions(
                onNext = {
                    instruments.firstOrNull()?.apply { setInsName(this.name) }
                    kpiNameFr.requestFocus()
                }
            ),
            keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next),
            focusRequester = insNameFr
        )
        SuggestionInputField(
            value = kpiName,
            label = "Nyckeltal",
            onValueChange = setKpiName,
            items = kpis,
            keyboardActions = KeyboardActions(
                onNext = {
                    kpis.firstOrNull()?.apply { setKpiName(this.name) }
                    kpiValueFr.requestFocus()
                }
            ),
            keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next),
            focusRequester = kpiNameFr
        )
        InputField(
            value = kpiValue,
            label = "Går under värde",
            onValueChange = setKpiValue,
            keyboardActions = KeyboardActions(
                onDone = { addAlarm() }
            ),
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Done
            ),
            focusRequester = kpiValueFr
        )
    }
}

@Preview
@Composable
fun AddAlarmPreview() {
    AppTheme {
        AddAlarmContent(
            insName = "Evolution",
            kpiName = "P/E",
            kpiValue = "20.5",
            instruments = listOf(),
            kpis = listOf(),
            setInsName = {},
            setKpiName = {},
            setKpiValue = {},
            addAlarm = {}
        )
    }
}
