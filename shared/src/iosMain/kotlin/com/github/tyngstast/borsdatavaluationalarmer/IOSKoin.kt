package com.github.tyngstast.borsdatavaluationalarmer

import com.github.tyngstast.borsdatavaluationalarmer.settings.Vault
import com.github.tyngstast.db.ValueAlarmerDb
import com.liftric.kvault.KVault
import com.russhwolf.settings.AppleSettings
import com.russhwolf.settings.Settings
import com.squareup.sqldelight.db.SqlDriver
import com.squareup.sqldelight.drivers.native.NativeSqliteDriver
import org.koin.core.KoinApplication
import org.koin.dsl.module
import platform.Foundation.NSUserDefaults

actual val platformModule = module {
    single<SqlDriver> { NativeSqliteDriver(ValueAlarmerDb.Schema, "ValueAlarmerDb") }
    single { Vault(KVault(null, null)) }
}

fun initKoinIos(
    userDefaults: NSUserDefaults,
): KoinApplication = initKoin(
    module {
        single<Settings> { AppleSettings(userDefaults) }
    }
)
