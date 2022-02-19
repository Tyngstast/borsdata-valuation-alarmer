package com.github.tyngstast.borsdatavaluationalarmer.android.ui.add

import android.widget.Toast
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import org.koin.androidx.compose.getViewModel


@Composable
fun AddAlarmScreen(
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

    val addAlarm = {
        if (insName.isBlank() || kpiName.isBlank() || kpiValue.isBlank()) {
            Toast.makeText(context, "Var god fyll i värden", Toast.LENGTH_SHORT).show()
        } else {
            viewModel.addAlarm()
            Toast.makeText(context, "Sparade nytt alarm", Toast.LENGTH_SHORT).show()
            onSuccess()
        }
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
                        Icon(
                            Icons.Filled.Check,
                            contentDescription = "Add",
                            tint = MaterialTheme.colors.secondary
                        )
                    }
                }
            )
        }
    ) {
        AddAlarmContent(
            insName = insName,
            kpiName = kpiName,
            kpiValue = kpiValue,
            instruments = instruments.value,
            kpis = kpis.value,
            setInsName = setInsName,
            setKpiName = setKpiName,
            setKpiValue = setKpiValue,
            addAlarm = addAlarm
        )
    }

}

