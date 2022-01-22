package com.github.tyngstast.borsdatavaluationalarmer.android.ui.add

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.koin.androidx.compose.getViewModel


@Composable
private fun InputField(
    value: String,
    label: String,
    onValueChange: (String) -> Unit,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default
) = TextField(
    value = value,
    singleLine = true,
    colors = TextFieldDefaults.textFieldColors(backgroundColor = Color.White),
    modifier = Modifier.fillMaxWidth(),
    textStyle = TextStyle(fontSize = 20.sp),
    label = { Text(label, fontSize = 16.sp) },
    onValueChange = onValueChange,
    keyboardOptions = keyboardOptions
)

class FormState {
    var insName: String by mutableStateOf("")
    var kpiName: String by mutableStateOf("")
    var kpiValue: String by mutableStateOf("")
}

@ExperimentalCoroutinesApi
@Composable
fun AddAlarm(
    popBack: () -> Unit,
    viewModel: AddAlarmViewModel = getViewModel()
) {
    var insName: String by remember { mutableStateOf("") }
    var kpiName: String by remember { mutableStateOf("") }
    var kpiValue: String by remember { mutableStateOf("") }

//    val setInsName = { value: String ->
//        insName = value
//        viewModel.setInsQuery(value)
//    }
//    val setKpiName = { value: String ->
//        kpiName = value
//        viewModel.setKpiQuery(value)
//    }
//    val setKpiValue = { value: String -> kpiValue = value }

//    val instruments = viewModel.instruments()
//        .filter {
//            insName.isNotBlank() &&
//                    (it.name.contains(insName, ignoreCase = true)
//                            || it.ticker.contains(insName, ignoreCase = true))
//        }
//        .sortedByDescending {
//            it.name.startsWith(insName, ignoreCase = true) ||
//                    it.ticker.startsWith(insName, ignoreCase = true)
//        }
//        .take(3)
//        .map { Item(it.id, it.name) }
//    val kpis = viewModel.kpis

//    Log.i("AddAlarm", "instruments: $instruments")

    fun addAlarm() {
        Log.i("AddAlarm", "${insName} ${kpiName} ${kpiValue}")
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
                    IconButton(onClick = { addAlarm() }) {
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
                onValueChange = { value -> insName = value},
                items = listOf()
            )
            Field(
                value = kpiName,
                label = "Nyckeltal",
                onValueChange = { value -> kpiName = value},
                items = listOf()
            )
            Field(
                value = kpiValue,
                label = "Värde",
                onValueChange = { value -> kpiValue = value},
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)
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
    suggestions: Boolean = true,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default
) {
    val focusManager = LocalFocusManager.current
    var showSuggestions: Boolean by remember { mutableStateOf(suggestions) }

    InputField(
        value = value,
        label = label,
        onValueChange = { v: String ->
            onValueChange(v)
            showSuggestions = true
        },
        keyboardOptions = keyboardOptions
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
                            focusManager.clearFocus()
                        },
                ) {
                    Text(text = item.name, modifier = Modifier.padding(8.dp))
                }
            }
        }
    }
}

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
