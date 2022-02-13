package com.github.tyngstast.borsdatavaluationalarmer.android.ui.list

import android.annotation.SuppressLint
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.flowWithLifecycle
import org.koin.androidx.compose.getViewModel

@Composable
fun AlarmListScreen(
    onAdd: () -> Unit,
    onResetKey: () -> Unit,
    viewModel: AlarmListViewModel = getViewModel()
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    val lifecycleAwareAlarmsFlow = remember(viewModel.alarms, lifecycleOwner) {
        viewModel.alarms.flowWithLifecycle(lifecycleOwner.lifecycle)
    }

    @SuppressLint("StateFlowValueCalledInComposition")
    val alarms by lifecycleAwareAlarmsFlow.collectAsState(viewModel.alarms.value)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Aktiva alarm") },
                actions = { Menu(onResetKey) }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onAdd) {
                Icon(Icons.Filled.Add, "")
            }
        }
    ) {
        AlarmListContent(
            alarms = alarms,
            updateDisableAlarm = viewModel.updateDisableAlarm,
            deleteAlarm = viewModel.deleteAlarm,
        )
    }
}

