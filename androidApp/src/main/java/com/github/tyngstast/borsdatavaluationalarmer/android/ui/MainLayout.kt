package com.github.tyngstast.borsdatavaluationalarmer.android.ui

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import com.github.tyngstast.borsdatavaluationalarmer.android.ui.add.AddAlarm
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.navigation.animation.rememberAnimatedNavController

sealed class Screen(val title: String) {
    object AlarmList : Screen("AlarmList")
    object AddAlarm : Screen("AddAlarm")
    object EditAlarm : Screen("EditAlarm")
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun MainLayout() {
    val navController = rememberAnimatedNavController()

    MaterialTheme {
        Scaffold { paddingValues ->
            AnimatedNavHost(navController, Screen.AlarmList.title) {
                composable(
                    route = Screen.AlarmList.title,
                    exitTransition = { slideOutHorizontally() },
                    popEnterTransition = { slideInHorizontally() }
                ) {
                    AlarmList(
                        paddingValues = paddingValues,
                        onAdd = { navController.navigate(Screen.AddAlarm.title) }
                    )
                }
                composable(
                    route = Screen.AddAlarm.title,
                    exitTransition = { slideOutHorizontally() },
                    popEnterTransition = { slideInHorizontally() }
                ) {
                    AddAlarm(
                        popBack = { navController.popBackStack() }
                    )
                }
            }
        }
    }
}
