package com.github.tyngstast.borsdatavaluationalarmer.android.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.github.tyngstast.borsdatavaluationalarmer.SharedModel
import com.github.tyngstast.borsdatavaluationalarmer.android.ui.theme.AppTheme
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch


class MainActivity : ComponentActivity() {

    private val scope = MainScope()
    private val sharedModel = SharedModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        scope.launch {
            sharedModel.initDummyData()
            sharedModel.updateInstrumentsAndKpisIfStale()
        }

        setContent {
            AppTheme {
                MainLayout()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        scope.cancel()
    }
}
