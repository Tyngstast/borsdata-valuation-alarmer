package com.github.tyngstast.borsdatavaluationalarmer.android

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.tyngstast.borsdatavaluationalarmer.SharedModel
import com.github.tyngstast.borsdatavaluationalarmer.db.AlarmDao
import com.github.tyngstast.db.Alarm
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class AlarmListViewModel : ViewModel(), KoinComponent {

    companion object {
        private const val TAG = "AlarmListViewModel"
    }

    private val alarmDao: AlarmDao by inject()
    private val sharedModel = SharedModel()

    val alarms: StateFlow<List<Alarm>> = alarmDao.getAllAlarmsAsFlow()
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())
}
