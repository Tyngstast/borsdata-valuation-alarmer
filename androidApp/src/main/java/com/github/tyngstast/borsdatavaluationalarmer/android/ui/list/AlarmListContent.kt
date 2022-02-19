package com.github.tyngstast.borsdatavaluationalarmer.android.ui.list

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.material.DismissDirection
import androidx.compose.material.DismissValue
import androidx.compose.material.Divider
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.FractionalThreshold
import androidx.compose.material.Icon
import androidx.compose.material.LocalTextStyle
import androidx.compose.material.MaterialTheme
import androidx.compose.material.SwipeToDismiss
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Update
import androidx.compose.material.icons.filled.UpdateDisabled
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.rememberDismissState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.Placeholder
import androidx.compose.ui.text.PlaceholderVerticalAlign
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.tyngstast.borsdatavaluationalarmer.android.ui.theme.deleteColor
import com.github.tyngstast.borsdatavaluationalarmer.android.ui.theme.disableColor
import com.github.tyngstast.borsdatavaluationalarmer.android.ui.theme.divider
import com.github.tyngstast.borsdatavaluationalarmer.android.ui.theme.enableColor
import com.github.tyngstast.borsdatavaluationalarmer.android.ui.theme.selectedColor
import com.github.tyngstast.borsdatavaluationalarmer.android.ui.theme.swipeBackground
import com.github.tyngstast.db.Alarm

@Composable
fun AlarmListContent(
    alarms: List<Alarm>,
    updateDisableAlarm: (Long, Boolean) -> Unit,
    deleteAlarm: (Long) -> Unit
) {
    if (alarms.isEmpty()) {
        WelcomeInfo()
    } else {
        AlarmList(
            alarms = alarms,
            updateDisableAlarm = updateDisableAlarm,
            deleteAlarm = deleteAlarm
        )
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun AlarmList(
    alarms: List<Alarm>,
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
                directions = setOf(DismissDirection.StartToEnd, DismissDirection.EndToStart),
                dismissThresholds = { direction ->
                    FractionalThreshold(if (direction == DismissDirection.StartToEnd) 0.15f else 0.25f)
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
                    Card(
                        shape = MaterialTheme.shapes.large,
                        elevation = animateDpAsState(
                            if (dismissState.dismissDirection != null) 4.dp else 0.dp
                        ).value
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
                                    Row(Modifier.clickable { onDisable() }) {
                                        val (icon, text) =
                                            if (disabled) Icons.Default.Update to "Återaktivera"
                                            else Icons.Default.UpdateDisabled to "Inaktivera"
                                        Icon(
                                            icon,
                                            contentDescription = text,
                                            modifier = Modifier.size(19.dp)
                                        )
                                        Text(
                                            text,
                                            modifier = Modifier.padding(horizontal = 4.dp),
                                            style = LocalTextStyle.current.copy(fontSize = 14.sp)
                                        )
                                    }
                                    Row(Modifier.clickable { deleteAlarm(alarm.id) }) {
                                        Icon(
                                            Icons.Outlined.Delete,
                                            contentDescription = "Ta bort",
                                            modifier = Modifier.size(19.dp)
                                        )
                                        Text(
                                            "Ta bort",
                                            modifier = Modifier.padding(horizontal = 4.dp),
                                            style = LocalTextStyle.current.copy(fontSize = 14.sp)
                                        )
                                    }
                                }
                            }
                            Divider(color = MaterialTheme.colors.divider, thickness = 1.dp)
                        }
                    }
                }
            )
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
        Text("Här var det tomt!")
        Spacer(Modifier.height(16.dp))
        Text("Lägg till ett alarm för att starta ny bevakning.", textAlign = TextAlign.Center)
        Spacer(Modifier.height(16.dp))
        Text(
            text = buildAnnotatedString {
                append("En ")
                appendInlineContent("bolt", "[bolt]")
                append(" markerar nyckeltal som räknas ut med nuvarande pris.")
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
                    Icon(Icons.Filled.Bolt, contentDescription = "Blixt")
                }
            )
        )
        Spacer(Modifier.height(16.dp))
        Text("Överiga nyckeltal uppdateras en gång per dag.", textAlign = TextAlign.Center)
    }
}
