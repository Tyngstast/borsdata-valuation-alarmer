package com.github.tyngstast.borsdatavaluationalarmer.android.ui.add

import androidx.lifecycle.ViewModel
import org.koin.core.component.KoinComponent

class AddAlarmViewModel: ViewModel(), KoinComponent {
}

data class Item(val id: Long, val name: String)