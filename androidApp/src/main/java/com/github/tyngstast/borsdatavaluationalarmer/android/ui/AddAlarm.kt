package com.github.tyngstast.borsdatavaluationalarmer.android.ui

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp


@Composable
private fun InputField(
    value: String,
    label: String,
    onValueChange: (String) -> Unit,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default
) {
    return TextField(
        value = value,
        singleLine = true,
        colors = TextFieldDefaults.textFieldColors(backgroundColor = Color.White),
        modifier = Modifier.fillMaxWidth(),
        textStyle = TextStyle(fontSize = 20.sp),
        label = { Text(label, fontSize = 16.sp) },
        onValueChange = onValueChange,
        keyboardOptions = keyboardOptions
    )
}

@Composable
fun AddAlarm(
    popBack: () -> Unit
) {
    var insName: String by remember { mutableStateOf("") }
    var kpiName: String by remember { mutableStateOf("") }
    var kpiValue: String by remember { mutableStateOf("") }

    fun addAlarm() {
        Log.i("AddAlarm", "$insName $kpiName $kpiValue")
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
            InputField(
                value = insName,
                label = "Bolag",
                onValueChange = { value -> insName = value.trim()}
            )
            InputField(
                value = kpiName,
                label = "Nyckeltal",
                onValueChange = { value -> kpiName = value.trim()}
            )
            InputField(
                value = kpiValue,
                label = "Värde",
                onValueChange = { value -> kpiValue = value.trim()},
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)
            )
        }
    }
}

@Preview
@Composable
fun AddAlarmPreview() {
    MaterialTheme {
        AddAlarm({})
    }
}
