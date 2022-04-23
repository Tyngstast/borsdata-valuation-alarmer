package com.github.tyngstast.borsdatavaluationalarmer.android.ui.list

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.foundation.text.appendInlineContent
import androidx.compose.material.Card
import androidx.compose.material.Divider
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.LocalTextStyle
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Update
import androidx.compose.material.icons.filled.UpdateDisabled
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.Placeholder
import androidx.compose.ui.text.PlaceholderVerticalAlign
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.tyngstast.borsdatavaluationalarmer.android.R
import com.github.tyngstast.borsdatavaluationalarmer.android.ui.list.AlarmListViewModel.AlarmListState
import com.github.tyngstast.borsdatavaluationalarmer.android.ui.list.AlarmListViewModel.AlarmListState.Loading
import com.github.tyngstast.borsdatavaluationalarmer.android.ui.list.AlarmListViewModel.AlarmListState.Success
import com.github.tyngstast.borsdatavaluationalarmer.android.ui.theme.divider
import com.github.tyngstast.borsdatavaluationalarmer.android.ui.theme.selectedColor
import com.github.tyngstast.db.Alarm
import org.koin.androidx.compose.getViewModel

@Composable
fun AlarmListScreen(
    onAdd: () -> Unit,
    onEdit: (Long) -> Unit,
    onResetKey: () -> Unit,
    viewModel: AlarmListViewModel = getViewModel()
) {
    val alarmListState by viewModel.alarmListState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.list_text_title)) },
                backgroundColor = MaterialTheme.colors.primaryVariant,
                actions = { Menu(onResetKey) }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onAdd) {
                Icon(Icons.Default.Add, stringResource(R.string.list_cd_add_button))
            }
        }
    ) {
        AlarmListContent(
            alarmListState = alarmListState,
            onEdit = onEdit,
            updateDisableAlarm = viewModel.updateDisableAlarm,
            deleteAlarm = viewModel.deleteAlarm,
        )
    }
}

@Composable
fun AlarmListContent(
    alarmListState: AlarmListState,
    onEdit: (Long) -> Unit,
    updateDisableAlarm: (Long, Boolean) -> Unit,
    deleteAlarm: (Long) -> Unit
) {
    if (alarmListState is Success && alarmListState.alarms.isNotEmpty()) {
        AlarmList(
            alarms = alarmListState.alarms,
            onEdit = onEdit,
            updateDisableAlarm = updateDisableAlarm,
            deleteAlarm = deleteAlarm
        )
    } else if (alarmListState !is Loading) {
        WelcomeInfo()
    } // else loading animation
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun AlarmList(
    alarms: List<Alarm>,
    onEdit: (Long) -> Unit,
    updateDisableAlarm: (Long, Boolean) -> Unit,
    deleteAlarm: (Long) -> Unit
) {
    val context = LocalContext.current

    var selectedRow: Alarm? by remember { mutableStateOf(null) }

    LazyColumn {
        items(alarms, { alarm: Alarm -> alarm.id }) { alarm ->
            var disabled: Boolean by remember { mutableStateOf(alarm.disabled ?: false) }
            val backgroundColor =
                if (selectedRow == alarm) MaterialTheme.colors.selectedColor
                else MaterialTheme.colors.background

            fun onDisable() {
                disabled = !disabled
                updateDisableAlarm(alarm.id, disabled)
                selectedRow = null
                Toast.makeText(
                    context,
                    context.getString(
                        if (disabled) R.string.list_toast_alarm_deacitvated
                        else R.string.list_toast_alarm_reacitvated
                    ),
                    Toast.LENGTH_SHORT
                ).show()
            }

            Card(
                shape = MaterialTheme.shapes.large,
//                elevation = animateDpAsState(
//                    if (dismissState.dismissDirection != null) 4.dp else 0.dp
//                ).value
            ) {
                Column(
                    Modifier.selectable(
                        selected = selectedRow == alarm,
                        onClick = {
                            selectedRow = if (selectedRow == alarm) null else alarm
                        }
                    ),
                ) {
                    AlarmItem(alarm, backgroundColor)
                    AnimatedVisibility(
                        visible = selectedRow == alarm,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .background(backgroundColor)
                                .padding(
                                    start = 12.dp,
                                    end = 12.dp,
                                    top = 4.dp,
                                    bottom = 12.dp
                                )
                        ) {
                            Row {
                                Row(Modifier.clickable { onEdit(alarm.id) }) {
                                    val editText = stringResource(R.string.list_edit_button)
                                    Icon(
                                        Icons.Outlined.Edit,
                                        contentDescription = editText,
                                        modifier = Modifier.size(19.dp)
                                    )
                                    Text(
                                        editText,
                                        modifier = Modifier.padding(horizontal = 2.dp),
                                        style = LocalTextStyle.current.copy(fontSize = 14.sp)
                                    )
                                }
                            }
                            Row {
                                val (icon, text) =
                                    if (disabled) Icons.Default.Update to context.getString(R.string.list_reactiveate_button)
                                    else Icons.Default.UpdateDisabled to context.getString(R.string.list_deactiveate_button)
                                Row(
                                    Modifier
                                        .clickable { onDisable() }
                                        .padding(horizontal = 12.dp)
                                ) {
                                    Icon(
                                        icon,
                                        contentDescription = text,
                                        modifier = Modifier.size(19.dp)
                                    )
                                    Text(
                                        text,
                                        modifier = Modifier.padding(horizontal = 2.dp),
                                        style = LocalTextStyle.current.copy(fontSize = 14.sp)
                                    )
                                }
                                Row(Modifier.clickable { deleteAlarm(alarm.id) }) {
                                    val deleteText = stringResource(R.string.list_delete_button)
                                    Icon(
                                        Icons.Outlined.Delete,
                                        contentDescription = deleteText,
                                        modifier = Modifier.size(19.dp)
                                    )
                                    Text(
                                        deleteText,
                                        modifier = Modifier.padding(horizontal = 2.dp),
                                        style = LocalTextStyle.current.copy(fontSize = 14.sp)
                                    )
                                }
                            }
                        }
                    }
                    Divider(color = MaterialTheme.colors.divider, thickness = 1.dp)
                }
            }

            /*
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
                directions = setOf(DismissDirection.StartToEnd, DismissDirection.EndToStart),
                dismissThresholds = { direction ->
                    FractionalThreshold(if (direction == DismissDirection.StartToEnd) 0.25f else 0.4f)
                },
                background = {
                    val direction = dismissState.dismissDirection ?: return@SwipeToDismiss
                    val color by animateColorAsState(
                        when (dismissState.targetValue) {
                            DismissValue.Default -> MaterialTheme.colors.swipeBackground
                            DismissValue.DismissedToStart -> MaterialTheme.colors.deleteColor
                            DismissValue.DismissedToEnd ->
                                if (disabled) MaterialTheme.colors.enableColor
                                else MaterialTheme.colors.disableColor
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
                }
            )
             */
        }
    }
}

@Composable
fun WelcomeInfo() {
    Column(
        Modifier
            .fillMaxWidth()
            .wrapContentSize(Alignment.Center)
            .padding(vertical = 24.dp, horizontal = 36.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Text(stringResource(R.string.welcome_p1))
        Spacer(Modifier.height(16.dp))
        Text(stringResource(R.string.welcome_p2), textAlign = TextAlign.Center)
        Spacer(Modifier.height(16.dp))
        Text(
            text = buildAnnotatedString {
                append(stringResource(R.string.welcome_p3_1))
                appendInlineContent("bolt", "[bolt]")
                append(stringResource(R.string.welcome_p3_2))
            },
            textAlign = TextAlign.Center,
            inlineContent = mapOf(
                "bolt" to InlineTextContent(
                    Placeholder(
                        width = 20.sp,
                        height = 20.sp,
                        placeholderVerticalAlign = PlaceholderVerticalAlign.Center
                    )
                ) {
                    Icon(Icons.Filled.Bolt, contentDescription = stringResource(R.string.welcome_p3_icon_cd))
                }
            )
        )
        Spacer(Modifier.height(16.dp))
        Text(stringResource(R.string.welcome_p4), textAlign = TextAlign.Center)
    }
}
