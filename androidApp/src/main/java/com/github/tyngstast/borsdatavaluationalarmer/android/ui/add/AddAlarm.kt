package com.github.tyngstast.borsdatavaluationalarmer.android.ui.add

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import com.github.tyngstast.borsdatavaluationalarmer.android.ui.theme.AppTheme
import org.koin.androidx.compose.getViewModel


@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun AddAlarm(
    onSuccess: () -> Unit,
    viewModel: AddAlarmViewModel = getViewModel()
) {
    val context = LocalContext.current

    var insName: String by remember { mutableStateOf("") }
    var kpiName: String by remember { mutableStateOf("") }
    var kpiValue: String by remember { mutableStateOf("") }

    val setInsName = { value: String ->
        insName = value
        viewModel.setInsName(value)
    }
    val setKpiName = { value: String ->
        kpiName = value
        viewModel.setKpiName(value)
    }
    val setKpiValue = { value: String ->
        kpiValue = value
        viewModel.setKpiValue(value)
    }

    val instruments = viewModel.instruments.collectAsState()
    val kpis = viewModel.kpis.collectAsState()

    val (insNameFr, kpiNameFr, kpiValueFr) = FocusRequester.createRefs()

    val addAlarm = {
        viewModel.addAlarm()
        Toast.makeText(context, "Sparade nytt alarm", Toast.LENGTH_SHORT).show()
        onSuccess()
    }

    LaunchedEffect(Unit) {
        insNameFr.requestFocus()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Lägg till alarm") },
                navigationIcon = {
                    IconButton(onClick = { onSuccess() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = addAlarm) {
                        Icon(Icons.Filled.Check, contentDescription = "Add")
                    }
                }
            )
        }
    ) {
        Column(
            Modifier
                .fillMaxWidth()
                .fillMaxHeight()
        ) {
            AddAlarmField(
                value = insName,
                label = "Bolag",
                onValueChange = setInsName,
                items = instruments.value,
                keyboardActions = KeyboardActions(
                    onNext = {
                        instruments.value.firstOrNull()?.apply { setInsName(this.name) }
                        kpiNameFr.requestFocus()
                    }
                ),
                keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next),
                focusRequester = insNameFr
            )
            AddAlarmField(
                value = kpiName,
                label = "Nyckeltal",
                onValueChange = setKpiName,
                items = kpis.value,
                keyboardActions = KeyboardActions(
                    onNext = {
                        kpis.value.firstOrNull()?.apply { setKpiName(this.name) }
                        kpiValueFr.requestFocus()
                    }
                ),
                keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next),
                focusRequester = kpiNameFr
            )
            AddAlarmField(
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
}

@Preview
@Composable
fun AddAlarmPreview() {
    AppTheme {
        Column(
            Modifier
                .fillMaxWidth()
                .fillMaxHeight()
        ) {
            AddAlarmField(
                value = "Evolution",
                label = "Bolag",
                onValueChange = {},
                items = listOf(
                    Item(1, "Evolution"),
                    Item(2, "Revolutionrace")
                )
            )
            AddAlarmField(
                value = "P/E",
                label = "Nyckeltal",
                onValueChange = {},
                items = listOf(
                    Item(1, "P/E"),
                    Item(2, "P/S")
                )
            )
            AddAlarmField(
                value = "20.5",
                label = "Värde",
                onValueChange = {},
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)
            )
        }
    }
}
