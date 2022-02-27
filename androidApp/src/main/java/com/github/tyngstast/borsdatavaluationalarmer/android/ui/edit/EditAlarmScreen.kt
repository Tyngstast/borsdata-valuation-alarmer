package com.github.tyngstast.borsdatavaluationalarmer.android.ui.edit

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import com.github.tyngstast.borsdatavaluationalarmer.android.ui.common.InputField
import org.koin.androidx.compose.getViewModel

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun EditAlarmScreen(
    id: Long,
    onSuccess: () -> Unit,
    viewModel: EditAlarmViewModel = getViewModel()
) {
    val context = LocalContext.current
    val kpiValueFr = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        kpiValueFr.requestFocus()
    }

    val alarm = viewModel.getAlarm(id)
    var kpiValue: String by remember { mutableStateOf(alarm.kpiValue.toString()) }

    val editAlarm = {
        if (kpiValue.isBlank()) {
            Toast.makeText(context, "Var god fyll i ett värde", Toast.LENGTH_SHORT).show()
        } else {
            viewModel.editAlarm(id, kpiValue)
            Toast.makeText(context, "Uppdaterade alarm", Toast.LENGTH_SHORT).show()
            onSuccess()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Redigera alarm") },
                navigationIcon = {
                    IconButton(onClick = { onSuccess() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = editAlarm) {
                        Icon(
                            Icons.Filled.Check,
                            contentDescription = "Redigera",
                            tint = MaterialTheme.colors.secondary
                        )
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
                value = alarm.insName,
                label = "Bolag",
                disabled = true
            )
            InputField(
                value = alarm.kpiName,
                label = "Nyckeltal",
                disabled = true
            )
            InputField(
                value = kpiValue,
                label = "Värde",
                onValueChange = { value: String -> kpiValue = value },
                keyboardActions = KeyboardActions(
                    onDone = { editAlarm() }
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
