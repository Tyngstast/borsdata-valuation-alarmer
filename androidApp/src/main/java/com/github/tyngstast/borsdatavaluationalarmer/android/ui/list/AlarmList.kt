package com.github.tyngstast.borsdatavaluationalarmer.android.ui.list

import android.annotation.SuppressLint
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
import androidx.compose.material.rememberDismissState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.lifecycle.flowWithLifecycle
import com.github.tyngstast.db.Alarm
import org.koin.androidx.compose.getViewModel

@OptIn(ExperimentalMaterialApi::class, ExperimentalFoundationApi::class)
@Composable
fun AlarmList(
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
        LazyColumn {
            items(alarms, { alarm: Alarm -> alarm.id }) { alarm ->
                val dismissState = rememberDismissState(
                    confirmStateChange = {
                        val confirmed = it == DismissValue.DismissedToStart
                        if (confirmed) {
                            viewModel.deleteAlarm(alarm.id)
                        }
                        confirmed
                    }
                )
                SwipeToDismiss(
                    state = dismissState,
                    modifier = Modifier.animateItemPlacement(),
                    directions = setOf(DismissDirection.EndToStart),
                    dismissThresholds = {
                        FractionalThreshold(0.33f)
                    },
                    background = {
                        val color by animateColorAsState(
                            when (dismissState.targetValue) {
                                DismissValue.DismissedToStart -> Color.Red
                                else -> Color.LightGray
                            }
                        )
                        val scale by animateFloatAsState(
                            if (dismissState.targetValue == DismissValue.Default) 0.75f else 1f
                        )

                        Box(
                            Modifier
                                .fillMaxSize()
                                .background(color)
                                .padding(horizontal = 20.dp),
                            contentAlignment = Alignment.CenterEnd
                        ) {
                            Icon(
                                Icons.Default.Delete,
                                contentDescription = "Ta bort Alarm",
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
}
