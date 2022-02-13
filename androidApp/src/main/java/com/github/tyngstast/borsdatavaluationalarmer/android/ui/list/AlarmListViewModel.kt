package com.github.tyngstast.borsdatavaluationalarmer.android.ui.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.touchlab.kermit.Logger
import com.github.tyngstast.borsdatavaluationalarmer.SharedModel
import com.github.tyngstast.borsdatavaluationalarmer.android.worker.TriggerWorkerMessagingService
import com.github.tyngstast.borsdatavaluationalarmer.db.AlarmDao
import com.github.tyngstast.borsdatavaluationalarmer.injectLogger
import com.github.tyngstast.db.Alarm
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.ktx.messaging
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class AlarmListViewModel : ViewModel(), KoinComponent {

    private val sharedModel = SharedModel()
    private val alarmDao: AlarmDao by inject()
    private val log: Logger by injectLogger("AlarmListViewModel")

    private val _alarms = MutableStateFlow<List<Alarm>>(emptyList())
    val alarms: StateFlow<List<Alarm>> = _alarms

    init {
        viewModelScope.launch {
            sharedModel.updateInstrumentsAndKpisIfStale()
        }
        viewModelScope.launch {
            alarmDao.getAllAlarmsAsFlow().collect {
                _alarms.value = it
            }
        }
        Firebase.messaging.subscribeToTopic(TriggerWorkerMessagingService.TRIGGER_TOPIC)
            .addOnCompleteListener { task ->
                log.d { "Subscribed to topic ${TriggerWorkerMessagingService.TRIGGER_TOPIC}: ${task.isSuccessful}" }
            }
    }

    val updateDisableAlarm = { id: Long, disable: Boolean ->
        alarmDao.updateDisableAlarm(id, disable)
    }

    val deleteAlarm = { id: Long ->
        alarmDao.deleteAlarm(id)
    }
}
