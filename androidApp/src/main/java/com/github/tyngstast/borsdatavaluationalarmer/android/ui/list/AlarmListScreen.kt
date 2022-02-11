package com.github.tyngstast.borsdatavaluationalarmer.android.ui.list

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.DismissDirection
import androidx.compose.material.DismissValue
import androidx.compose.material.Divider
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.FractionalThreshold
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.material.SwipeToDismiss
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Update
import androidx.compose.material.icons.filled.UpdateDisabled
import androidx.compose.material.rememberDismissState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.lifecycle.flowWithLifecycle
import com.github.tyngstast.db.Alarm
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
            alarms,
            viewModel.disableAlarm,
            viewModel.deleteAlarm,
        )
    }
}

@OptIn(ExperimentalMaterialApi::class, ExperimentalFoundationApi::class)
@Composable
fun AlarmListContent(
    alarms: List<Alarm>,
    disableAlarm: (Long, Boolean) -> Unit,
    deleteAlarm: (Long) -> Unit
) {
    val context = LocalContext.current

    LazyColumn {
        items(alarms, { alarm: Alarm -> alarm.id }) { alarm ->
            var disabled: Boolean by remember { mutableStateOf(alarm.disabled ?: false) }
            fun onDisable() {
                disabled = !disabled
                disableAlarm(alarm.id, disabled)
                Toast.makeText(
                    context,
                    "Alarm ${if (disabled) "inaktiverat" else "återaktiverat"}!",
                    Toast.LENGTH_SHORT
                ).show()
            }

            val dismissState = rememberDismissState(
                confirmStateChange = {
                    val delete = it == DismissValue.DismissedToStart
                    val disable = it == DismissValue.DismissedToEnd
                    if (disable) onDisable()
                    else if (delete) deleteAlarm(alarm.id)
                    delete
                }
            )
            SwipeToDismiss(
                state = dismissState,
                modifier = Modifier.animateItemPlacement(),
                directions = setOf(DismissDirection.StartToEnd, DismissDirection.EndToStart),
                dismissThresholds = { direction ->
                    FractionalThreshold(if (direction == DismissDirection.StartToEnd) 0.15f else 0.3f)
                },
                background = {
                    val direction = dismissState.dismissDirection ?: return@SwipeToDismiss
                    val color by animateColorAsState(
                        when (dismissState.targetValue) {
                            DismissValue.Default -> Color.LightGray
                            DismissValue.DismissedToStart -> Color.Red
                            DismissValue.DismissedToEnd ->
                                if (disabled) Color.Green
                                else Color.Yellow
                        }
                    )
                    val alignment = when (direction) {
                        DismissDirection.StartToEnd -> Alignment.CenterStart
                        DismissDirection.EndToStart -> Alignment.CenterEnd
                    }
                    val (icon, description) = when (direction) {
                        DismissDirection.StartToEnd ->
                            if (disabled) Icons.Default.Update to "Aktivera Alarm"
                            else Icons.Default.UpdateDisabled to "Inaktivera Alarm"
                        DismissDirection.EndToStart -> Icons.Default.Delete to "Ta bort Alarm"
                    }
                    val scale by animateFloatAsState(
                        if (dismissState.targetValue == DismissValue.Default) 0.75f else 1f
                    )
                    Box(
                        Modifier
                            .fillMaxSize()
                            .background(color)
                            .padding(horizontal = 20.dp),
                        contentAlignment = alignment
                    ) {
                        Icon(
                            icon,
                            contentDescription = description,
                            modifier = Modifier.scale(scale)
                        )
                    }
                },
                dismissContent = {
                    Column {
                        AlarmCard(alarm)
                        Divider(color = Color.LightGray, thickness = 1.dp)
                    }
                }
            )
        }
    }
}
