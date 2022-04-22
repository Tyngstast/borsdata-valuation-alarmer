package com.github.tyngstast.borsdatavaluationalarmer.android.ui.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.touchlab.kermit.Logger
import com.github.tyngstast.borsdatavaluationalarmer.AlarmerSdk
import com.github.tyngstast.borsdatavaluationalarmer.android.messaging.TriggerWorkerMessagingService.Companion.TRIGGER_TOPIC
import com.github.tyngstast.borsdatavaluationalarmer.db.AlarmDao
import com.github.tyngstast.borsdatavaluationalarmer.injectLogger
import com.github.tyngstast.db.Alarm
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.ktx.messaging
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class AlarmListViewModel : ViewModel(), KoinComponent {

    private val alarmerSdk = AlarmerSdk()
    private val alarmDao: AlarmDao by inject()
    private val log: Logger by injectLogger("AlarmListViewModel")

    private val _alarmListState = MutableStateFlow<AlarmListState>(AlarmListState.Loading)
    val alarmListState = _alarmListState.stateIn(
        viewModelScope,
        SharingStarted.Eagerly,
        AlarmListState.Loading
    )

    init {
        viewModelScope.launch {
            alarmerSdk.updateInstrumentsAndKpisIfStale()
        }
        viewModelScope.launch {
            alarmDao.getAllAlarmsAsFlow().collect {
                _alarmListState.value = AlarmListState.Success(it)
            }
        }
        Firebase.messaging.subscribeToTopic(TRIGGER_TOPIC)
            .addOnCompleteListener { task ->
                log.d { "Subscribed to topic $TRIGGER_TOPIC: ${task.isSuccessful}. Also resetting failure counter" }
                alarmerSdk.resetFailureCounter()
            }
    }

    val updateDisableAlarm = { id: Long, disable: Boolean ->
        alarmDao.updateDisableAlarm(id, disable)
    }

    val deleteAlarm = { id: Long ->
        alarmDao.deleteAlarm(id)
    }

    sealed class AlarmListState {
        object Loading: AlarmListState()
        data class Success(val alarms: List<Alarm>): AlarmListState()
    }
}
