package com.github.tyngstast.borsdatavaluationalarmer.android.ui.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.tyngstast.borsdatavaluationalarmer.SharedModel
import com.github.tyngstast.borsdatavaluationalarmer.db.AlarmDao
import com.github.tyngstast.db.Alarm
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class AlarmListViewModel : ViewModel(), KoinComponent {

    private val sharedModel = SharedModel()
    private val alarmDao: AlarmDao by inject()

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
    }

    fun deleteAlarm(id: Long) {
        alarmDao.deleteAlarm(id)
    }
}
