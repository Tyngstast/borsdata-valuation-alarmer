package com.github.tyngstast.borsdatavaluationalarmer.android

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.tyngstast.borsdatavaluationalarmer.AlarmDao
import com.github.tyngstast.borsdatavaluationalarmer.BorsdataApi
import com.github.tyngstast.borsdatavaluationalarmer.Vault
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class AlarmViewModel : ViewModel(), KoinComponent{

    companion object {
        private const val TAG = "AlarmViewModel"
    }

    private val scope = viewModelScope
    private val alarmDao: AlarmDao by inject()
    private val borsdataApi: BorsdataApi by inject()
    private val vault: Vault by inject()

    init {
        // update KPIS and instruments in DB
    }

    val alarms = alarmDao.getAllAlarmsAsFlow()
        .stateIn(scope, SharingStarted.Eagerly, emptyList())

    fun initData() {
        val apiKey = vault.getApiKey()
        Log.i(TAG, "key: $apiKey")

        if (apiKey.isNullOrBlank()) {
            vault.setApiKey("redacted")
        }

        val alarms = alarmDao.getAllAlarms()
        if (alarms.isEmpty()) {
            alarmDao.insertAlarm(750, "Evolution", 2, "P/E", 40.0, "lte")
            alarmDao.insertAlarm(408, "Kambi", 2, "P/E", 30.0, "lte")
        }

        Log.i(TAG, "alarms: ${alarmDao.getAllAlarms()}")
    }
}