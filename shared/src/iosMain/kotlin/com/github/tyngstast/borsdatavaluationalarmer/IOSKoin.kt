package com.github.tyngstast.borsdatavaluationalarmer

import co.touchlab.kermit.Logger
import com.github.tyngstast.borsdatavaluationalarmer.model.AlarmListCallbackViewModel
import com.github.tyngstast.borsdatavaluationalarmer.model.LoginCallbackViewModel
import com.github.tyngstast.borsdatavaluationalarmer.settings.Vault
import com.github.tyngstast.db.ValueAlarmerDb
import com.liftric.kvault.KVault
import com.russhwolf.settings.AppleSettings
import com.russhwolf.settings.Settings
import com.squareup.sqldelight.db.SqlDriver
import com.squareup.sqldelight.drivers.native.NativeSqliteDriver
import org.koin.core.Koin
import org.koin.core.KoinApplication
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.koin.core.parameter.parametersOf
import org.koin.dsl.module
import platform.Foundation.NSUserDefaults

actual val platformModule = module {
    single<SqlDriver> { NativeSqliteDriver(ValueAlarmerDb.Schema, "ValueAlarmerDb") }
    single { Vault(KVault(null, null)) }
    single { LoginCallbackViewModel(get()) }
    single { AlarmListCallbackViewModel(get()) }
}

@Suppress("unused") // Called from Swift
fun initKoinIos(
    userDefaults: NSUserDefaults,
): KoinApplication = initKoin(
    module {
        single<Settings> { AppleSettings(userDefaults) }
    }
)

@Suppress("unused") // Access from Swift to create a logger
fun Koin.loggerWithTag(tag: String) =
    get<Logger>(qualifier = null) { parametersOf(tag) }

@Suppress("unused") // Called from Swift
object ViewModels : KoinComponent {
    fun getLoginViewModel() = get<LoginCallbackViewModel>()
    fun getAlarmListViewModel() = get<AlarmListCallbackViewModel>()
}
