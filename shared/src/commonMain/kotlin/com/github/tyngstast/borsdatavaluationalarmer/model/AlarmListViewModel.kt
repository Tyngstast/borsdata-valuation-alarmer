package com.github.tyngstast.borsdatavaluationalarmer.model

import com.github.tyngstast.db.Alarm
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

open class AlarmListViewModel(
    private val alarmListModel: AlarmListModel
) : ViewModel() {

    private val _alarmListStateFlow = MutableStateFlow<AlarmListState>(AlarmListState.Loading)
    val alarmListStateFlow: StateFlow<AlarmListState> = _alarmListStateFlow

    init {
        viewModelScope.launch {
            alarmListModel.updateInstrumentsAndKpisIfStale()
        }
        viewModelScope.launch {
            alarmListModel.getAll().collect {
                _alarmListStateFlow.value = AlarmListState.Success(it)
            }
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
