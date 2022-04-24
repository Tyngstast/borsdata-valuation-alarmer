package com.github.tyngstast.borsdatavaluationalarmer.android.ui.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.touchlab.kermit.Logger
import com.github.tyngstast.borsdatavaluationalarmer.android.messaging.TriggerWorkerMessagingService.Companion.TRIGGER_TOPIC
import com.github.tyngstast.borsdatavaluationalarmer.model.AlarmListModel
import com.github.tyngstast.db.Alarm
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.ktx.messaging
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class AlarmListViewModel(
    private val log: Logger,
    private val alarmListModel: AlarmListModel
) : ViewModel() {

    private val _alarmListState = MutableStateFlow<AlarmListState>(AlarmListState.Loading)
    val alarmListState = _alarmListState.stateIn(
        viewModelScope,
        SharingStarted.Eagerly,
        AlarmListState.Loading
    )

    init {
        viewModelScope.launch {
            alarmListModel.updateInstrumentsAndKpisIfStale()
        }
        viewModelScope.launch {
            alarmListModel.getAll().collect {
                _alarmListState.value = AlarmListState.Success(it)
            }
        }
        Firebase.messaging.subscribeToTopic(TRIGGER_TOPIC)
            .addOnCompleteListener { task ->
                log.d { "Subscribed to topic $TRIGGER_TOPIC: ${task.isSuccessful}. Also resetting failure counter" }
                alarmListModel.resetFailureCounter()
            }.addOnFailureListener {
                log.e(it) { "Failed to subscribe to topic" }
            }
    }

    val updateDisableAlarm = { id: Long, disable: Boolean ->
        alarmListModel.updateDisableAlarm(id, disable)
    }

    val deleteAlarm = { id: Long ->
        alarmListModel.deleteAlarm(id)
    }

    sealed class AlarmListState {
        object Loading : AlarmListState()
        data class Success(val alarms: List<Alarm>) : AlarmListState()
    }
}
