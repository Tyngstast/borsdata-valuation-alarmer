package com.github.tyngstast.borsdatavaluationalarmer.android.ui.add

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.FocusState
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.koin.androidx.compose.getViewModel


@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun AddAlarm(
    popBack: () -> Unit,
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

    val (kpiNameFocusRequester, kpiValueFocusRequester) = FocusRequester.createRefs()

    val addAlarm = {
        viewModel.addAlarm()
        Toast.makeText(context, "Added Alarm", Toast.LENGTH_SHORT).show()
        popBack()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Add New Alarm") },
                navigationIcon = {
                    IconButton(onClick = { popBack() }) {
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
            Field(
                value = insName,
                label = "Bolag",
                onValueChange = setInsName,
                items = instruments.value,
                keyboardActions = KeyboardActions(
                    onNext = {
                        instruments.value.firstOrNull()?.apply { setInsName(this.name) }
                        kpiNameFocusRequester.requestFocus()
                    }
                ),
                keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next)
            )
            Field(
                value = kpiName,
                label = "Nyckeltal",
                onValueChange = setKpiName,
                items = kpis.value,
                keyboardActions = KeyboardActions(
                    onNext = {
                        kpis.value.firstOrNull()?.apply { setKpiName(this.name) }
                        kpiValueFocusRequester.requestFocus()
                    }
                ),
                keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next),
                focusRequester = kpiNameFocusRequester
            )
            Field(
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
                focusRequester = kpiValueFocusRequester
            )
        }
    }
}

@Composable
fun Field(
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


@Preview
@Composable
fun AddAlarmPreview() {
    MaterialTheme {
        Column(
            Modifier
                .fillMaxWidth()
                .fillMaxHeight()
        ) {
            Field(
                value = "Evolution",
                label = "Bolag",
                onValueChange = {},
                items = listOf(
                    Item(1, "Evolution"),
                    Item(2, "Revolutionrace")
                )
            )
            Field(
                value = "P/E",
                label = "Nyckeltal",
                onValueChange = {},
                items = listOf(
                    Item(1, "P/E"),
                    Item(2, "P/S")
                )
            )
            Field(
                value = "20.5",
                label = "Värde",
                onValueChange = {},
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)
            )
        }
    }
}
