package com.github.tyngstast.borsdatavaluationalarmer.android

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.tyngstast.borsdatavaluationalarmer.AlarmModel
import com.github.tyngstast.borsdatavaluationalarmer.BorsdataApi
import com.github.tyngstast.borsdatavaluationalarmer.Vault
import com.github.tyngstast.borsdatavaluationalarmer.db.AlarmDao
import com.github.tyngstast.borsdatavaluationalarmer.db.InstrumentDao
import com.github.tyngstast.borsdatavaluationalarmer.db.KpiDao
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class AlarmViewModel : ViewModel(), KoinComponent{

    companion object {
        private const val TAG = "AlarmViewModel"
    }

    private val mainScope = MainScope()
    private val alarmDao: AlarmDao by inject()
    private val instrumentDao: InstrumentDao by inject()
    private val kpiDao: KpiDao by inject()
    private val borsdataApi: BorsdataApi by inject()
    private val vault: Vault by inject()
    private val alarmModel = AlarmModel()

    init {
        alarmModel.initDummyData()
        mainScope.launch {
            alarmModel.updateInstrumentsAndKpisIfStale()
        }
    }

    val alarms = alarmDao.getAllAlarmsAsFlow()
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())
}