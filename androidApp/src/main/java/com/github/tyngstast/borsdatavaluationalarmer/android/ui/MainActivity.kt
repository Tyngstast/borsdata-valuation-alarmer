package com.github.tyngstast.borsdatavaluationalarmer.android.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.github.tyngstast.borsdatavaluationalarmer.android.ui.theme.AppTheme
import org.koin.core.component.KoinComponent

class MainActivity : ComponentActivity(), KoinComponent {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            AppTheme {
                MainLayout()
            }
        }
    }
}