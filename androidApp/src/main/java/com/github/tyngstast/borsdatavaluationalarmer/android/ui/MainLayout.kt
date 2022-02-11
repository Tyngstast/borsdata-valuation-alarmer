package com.github.tyngstast.borsdatavaluationalarmer.android.ui

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import com.github.tyngstast.borsdatavaluationalarmer.android.ui.add.AddAlarmScreen
import com.github.tyngstast.borsdatavaluationalarmer.android.ui.list.AlarmListScreen
import com.github.tyngstast.borsdatavaluationalarmer.android.ui.login.LoginScreen
import com.github.tyngstast.borsdatavaluationalarmer.android.ui.login.LoginViewModel
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import org.koin.androidx.compose.getViewModel

sealed class Screen(val title: String) {
    object Login : Screen("Login")
    object AlarmList : Screen("AlarmList")
    object AddAlarm : Screen("AddAlarm")
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun MainLayout(
    viewModel: LoginViewModel = getViewModel()
) {
    val navController = rememberAnimatedNavController()

    val apiKeyState by remember(viewModel) { viewModel.apiKeyState }.collectAsState()
    val start = if (apiKeyState.apiKey.isNotBlank()) Screen.AlarmList.title else Screen.Login.title

    val loginSuccess = {
        navController.popBackStack(Screen.Login.title, true)
        navController.graph.setStartDestination(Screen.AlarmList.title)
        navController.navigate(Screen.AlarmList.title)
    }

    val resetKey = {
        viewModel.clearKey()
        navController.popBackStack(Screen.AlarmList.title, true)
        navController.graph.setStartDestination(Screen.Login.title)
        navController.navigate(Screen.Login.title)
    }

    Scaffold {
        AnimatedNavHost(navController, startDestination = start) {
            composable(
                route = Screen.Login.title,
                exitTransition = { slideOutHorizontally() },
                popEnterTransition = { slideInHorizontally() }
            ) {
                LoginScreen(onSuccess = loginSuccess)
            }
            composable(
                route = Screen.AlarmList.title,
                exitTransition = { slideOutHorizontally() },
                popEnterTransition = { slideInHorizontally() }
            ) {
                AlarmListScreen(
                    onAdd = { navController.navigate(Screen.AddAlarm.title) },
                    onResetKey = resetKey
                )
            }
            composable(
                route = Screen.AddAlarm.title,
                exitTransition = { slideOutHorizontally() },
                popEnterTransition = { slideInHorizontally() }
            ) {
                AddAlarmScreen(onSuccess = { navController.popBackStack() })
            }
        }
    }
}
