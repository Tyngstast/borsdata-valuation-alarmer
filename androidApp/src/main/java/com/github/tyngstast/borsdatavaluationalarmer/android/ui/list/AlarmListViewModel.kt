package com.github.tyngstast.borsdatavaluationalarmer.android.ui.list

import co.touchlab.kermit.Logger
import com.github.tyngstast.borsdatavaluationalarmer.android.messaging.TriggerWorkerMessagingService.Companion.TRIGGER_TOPIC
import com.github.tyngstast.borsdatavaluationalarmer.model.AlarmListModel
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.ktx.messaging
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import com.github.tyngstast.borsdatavaluationalarmer.model.AlarmListViewModel as BaseAlarmListViewModel

class AlarmListViewModel(
    private val log: Logger,
    alarmListModel: AlarmListModel
) : BaseAlarmListViewModel(alarmListModel) {

    val alarmListState = alarmListStateFlow.stateIn(
        viewModelScope,
        SharingStarted.Eagerly,
        AlarmListState.Loading
    )

    init {
        Firebase.messaging.subscribeToTopic(TRIGGER_TOPIC)
            .addOnCompleteListener { task ->
                log.d { "Subscribed to topic ${TRIGGER_TOPIC}: ${task.isSuccessful}. Also resetting failure counter" }
                alarmListModel.resetFailureCounter()
            }.addOnFailureListener {
                log.e(it) { "Failed to subscribe to topic" }
            }
    }

}