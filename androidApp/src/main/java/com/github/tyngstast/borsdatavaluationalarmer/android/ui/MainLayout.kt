package com.github.tyngstast.borsdatavaluationalarmer.android.ui

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.github.tyngstast.borsdatavaluationalarmer.android.messaging.TriggerWorkerMessagingService.Companion.TRIGGER_TOPIC
import com.github.tyngstast.borsdatavaluationalarmer.android.ui.add.AddAlarmScreen
import com.github.tyngstast.borsdatavaluationalarmer.android.ui.edit.EditAlarmScreen
import com.github.tyngstast.borsdatavaluationalarmer.android.ui.list.AlarmListScreen
import com.github.tyngstast.borsdatavaluationalarmer.android.ui.login.LoginScreen
import com.github.tyngstast.borsdatavaluationalarmer.android.ui.login.LoginViewModel
import com.github.tyngstast.borsdatavaluationalarmer.model.LoginModel.ApiKeyState
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.ktx.messaging
import org.koin.androidx.compose.getViewModel

sealed class Screen(val title: String) {
    object Login : Screen("login")
    object AlarmList : Screen("list")
    object AddAlarm : Screen("add")
    object EditAlarm : Screen("edit")
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun MainLayout(
    viewModel: LoginViewModel = getViewModel()
) {
    val navController = rememberAnimatedNavController()
    val systemUiController = rememberSystemUiController()
    val statusBarColor = MaterialTheme.colors.primaryVariant
    val navColor = MaterialTheme.colors.background

    SideEffect {
        systemUiController.setStatusBarColor(color = statusBarColor, darkIcons = false)
        systemUiController.setNavigationBarColor(color = navColor)
    }

    val apiKeyState by remember(viewModel) { viewModel.apiKeyState }.collectAsState()
    val start = if (apiKeyState is ApiKeyState.Success) Screen.AlarmList.title else Screen.Login.title

    val loginSuccess = {
        navController.popBackStack(Screen.Login.title, true)
        navController.graph.setStartDestination(Screen.AlarmList.title)
        navController.navigate(Screen.AlarmList.title)
    }

    val resetKey = {
        viewModel.clearKey()
        Firebase.messaging.unsubscribeFromTopic(TRIGGER_TOPIC)
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
                    onEdit = { id: Long -> navController.navigate("${Screen.EditAlarm.title}/$id") },
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
            composable(
                route = "${Screen.EditAlarm.title}/{id}",
                arguments = listOf(navArgument("id") { type = NavType.LongType }),
                exitTransition = { slideOutHorizontally() },
                popEnterTransition = { slideInHorizontally() }
            ) { backstackEntry ->
                EditAlarmScreen(
                    id = backstackEntry.arguments!!.getLong("id"),
                    onSuccess = { navController.popBackStack() }
                )
            }
        }
    }
}
