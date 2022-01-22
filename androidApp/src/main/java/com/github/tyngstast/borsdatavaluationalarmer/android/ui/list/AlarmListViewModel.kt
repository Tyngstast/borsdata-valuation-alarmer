package com.github.tyngstast.borsdatavaluationalarmer.android

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.tyngstast.borsdatavaluationalarmer.AlarmModel
import com.github.tyngstast.borsdatavaluationalarmer.db.AlarmDao
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class AlarmListViewModel : ViewModel(), KoinComponent {

    companion object {
        private const val TAG = "AlarmListViewModel"
    }

    private val mainScope = MainScope()
    private val alarmDao: AlarmDao by inject()
    private val alarmModel = AlarmModel()

    init {
        mainScope.launch {
            alarmModel.updateInstrumentsAndKpisIfStale()
        }
    }

    val alarms = alarmDao.getAllAlarmsAsFlow()
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    fun initDummyData() {
        alarmModel.initDummyData()
    }
}
