package com.github.tyngstast.borsdatavaluationalarmer

import com.github.tyngstast.borsdatavaluationalarmer.settings.Vault
import com.github.tyngstast.db.ValueAlarmerDb
import com.liftric.kvault.KVault
import com.russhwolf.settings.AndroidSettings
import com.russhwolf.settings.Settings
import com.squareup.sqldelight.android.AndroidSqliteDriver
import com.squareup.sqldelight.db.SqlDriver
import org.koin.core.module.Module
import org.koin.dsl.module

actual val platformModule: Module = module {
    single<SqlDriver> {
        AndroidSqliteDriver(
            ValueAlarmerDb.Schema,
            get(),
            "ValueAlarmerDb"
        )
    }
    single<Settings> { AndroidSettings(get()) }
    single { Vault(KVault(get())) }
}
