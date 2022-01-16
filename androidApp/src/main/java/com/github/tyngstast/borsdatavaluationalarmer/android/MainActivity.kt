package com.github.tyngstast.borsdatavaluationalarmer.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.github.tyngstast.borsdatavaluationalarmer.android.ui.MainLayout
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.component.KoinComponent


class MainActivity : ComponentActivity(), KoinComponent {

    private val viewModel: AlarmViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel.initData()

        setContent {
            MainLayout()
        }
    }
}
