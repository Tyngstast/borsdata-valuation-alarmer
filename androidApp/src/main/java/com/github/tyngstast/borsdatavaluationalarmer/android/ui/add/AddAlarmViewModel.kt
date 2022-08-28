package com.github.tyngstast.borsdatavaluationalarmer.android.ui.add

import com.github.tyngstast.borsdatavaluationalarmer.model.AddAlarmModel
import com.github.tyngstast.borsdatavaluationalarmer.model.BaseAddAlarmViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn

class AddAlarmViewModel(addAlarmModel: AddAlarmModel) : BaseAddAlarmViewModel(addAlarmModel ) {

    val instruments = instrumentStateFlow.stateIn(
        viewModelScope,
        SharingStarted.Eagerly,
        emptyList()
    )

    val kpis = kpiStateFlow.stateIn(
        viewModelScope,
        SharingStarted.Eagerly,
        emptyList()
    )
}
