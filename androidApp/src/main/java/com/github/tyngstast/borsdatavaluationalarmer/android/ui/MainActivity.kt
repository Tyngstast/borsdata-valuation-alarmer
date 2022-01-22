package com.github.tyngstast.borsdatavaluationalarmer.android.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.github.tyngstast.borsdatavaluationalarmer.AlarmModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch


@ExperimentalCoroutinesApi
class MainActivity : ComponentActivity() {

    private val scope = MainScope()
    private val alarmModel = AlarmModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        scope.launch {
            alarmModel.initDummyData()
            alarmModel.updateInstrumentsAndKpisIfStale()
        }

        setContent {
            MainLayout()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        scope.cancel()
    }
}
