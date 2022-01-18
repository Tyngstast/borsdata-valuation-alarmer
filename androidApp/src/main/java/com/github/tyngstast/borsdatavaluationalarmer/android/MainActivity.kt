package com.github.tyngstast.borsdatavaluationalarmer.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.github.tyngstast.borsdatavaluationalarmer.android.ui.MainLayout
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel


class MainActivity : ComponentActivity() {

    private val scope = MainScope()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MainLayout()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        scope.cancel()
    }
}
