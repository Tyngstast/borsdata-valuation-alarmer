package com.github.tyngstast.borsdatavaluationalarmer.android.ui.add

import android.annotation.SuppressLint
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import com.github.tyngstast.borsdatavaluationalarmer.android.R
import com.github.tyngstast.borsdatavaluationalarmer.android.ui.theme.AppTheme
import com.github.tyngstast.borsdatavaluationalarmer.model.Item
import com.github.tyngstast.borsdatavaluationalarmer.util.isDouble
import org.koin.androidx.compose.getViewModel


@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun AddAlarmScreen(
    onSuccess: () -> Unit,
    viewModel: AddAlarmViewModel = getViewModel()
) {
    val context = LocalContext.current

    var insName: String by remember { mutableStateOf("") }
    var kpiName: String by remember { mutableStateOf("") }
    var kpiValue: String by remember { mutableStateOf("") }
    var isAbove: Boolean by remember { mutableStateOf(false) }

    val setInsName: (String) -> Unit = { value: String ->
        insName = value
        viewModel.getInstruments(value)
    }
    val setKpiName: (String) -> Unit = { value: String ->
        kpiName = value
        viewModel.getKpis(value)
    }
    val setKpiValue = { value: String ->
        kpiValue = value
    }

    val instruments = viewModel.instruments.collectAsState()
    val kpis = viewModel.kpis.collectAsState()

    val addAlarm = {
        if (insName.isBlank() || kpiName.isBlank() || kpiValue.isBlank()) {
            Toast.makeText(
                context,
                context.getString(R.string.add_toast_empty_input),
                Toast.LENGTH_SHORT
            ).show()
        } else if (!kpiValue.isDouble()) {
            Toast.makeText(
                context,
                context.getString(R.string.add_toast_invalid_kpi_value),
                Toast.LENGTH_SHORT
            ).show()
        } else {
            viewModel.addAlarm(kpiValue.replace(",", ".").toDouble(), isAbove)
            Toast.makeText(
                context,
                context.getString(R.string.add_toast_save_success),
                Toast.LENGTH_SHORT
            ).show()
            onSuccess()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.add_text_title)) },
                navigationIcon = {
                    IconButton(onClick = { onSuccess() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = stringResource(R.string.icon_cd_back))
                    }
                },
                actions = {
                    IconButton(onClick = addAlarm) {
                        Icon(
                            Icons.Filled.Check,
                            contentDescription = stringResource(R.string.icon_cd_save),
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
            isAbove = isAbove,
            instruments = instruments.value,
            kpis = kpis.value,
            setInsName = setInsName,
            setKpiName = setKpiName,
            setKpiValue = setKpiValue,
            toggleIsAbove = { isAbove = !isAbove },
            addAlarm = addAlarm
        )
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun AddAlarmContent(
    insName: String,
    kpiName: String,
    kpiValue: String,
    isAbove: Boolean,
    instruments: List<Item>,
    kpis: List<Item>,
    setInsName: (String) -> Unit,
    setKpiName: (String) -> Unit,
    setKpiValue: (String) -> Unit,
    toggleIsAbove: () -> Unit,
    addAlarm: () -> Unit,
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
            label = stringResource(R.string.company_label),
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
            label = stringResource(R.string.kpi_label),
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
        ValueInputField(
            value = kpiValue,
            isAbove = isAbove,
            toggleIsAbove = toggleIsAbove,
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
            isAbove = false,
            instruments = listOf(),
            kpis = listOf(),
            setInsName = {},
            setKpiName = {},
            setKpiValue = {},
            toggleIsAbove = {},
            addAlarm = {}
        )
    }
}
