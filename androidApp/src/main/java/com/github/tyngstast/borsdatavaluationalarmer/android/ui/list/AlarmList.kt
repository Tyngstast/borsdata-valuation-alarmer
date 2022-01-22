package com.github.tyngstast.borsdatavaluationalarmer.android.ui

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.github.tyngstast.borsdatavaluationalarmer.android.AlarmListViewModel
import org.koin.androidx.compose.getViewModel

@Composable
fun AlarmList(
    paddingValues: PaddingValues = PaddingValues(),
    onAdd: () -> Unit,
    alarmListViewModel: AlarmListViewModel = getViewModel()
) {
    val alarms = alarmListViewModel.alarms.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Active Alarms") })
        },
        floatingActionButton = {
            FloatingActionButton( onClick = onAdd) {
                Icon(Icons.Filled.Add, "")
            }
        }
    ) {
        LazyColumn(
            contentPadding = paddingValues,
            modifier = Modifier.padding(vertical = 2.dp)
        ) {
            items(alarms.value) { alarm ->
                AlarmView(alarm)
            }
        }
    }
}
