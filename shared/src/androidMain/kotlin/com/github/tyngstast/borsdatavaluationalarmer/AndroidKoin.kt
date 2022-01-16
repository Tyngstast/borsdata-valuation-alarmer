package com.github.tyngstast.borsdatavaluationalarmer

import com.github.tyngstast.db.ValueAlarmerDb
import com.liftric.kvault.KVault
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
    single {
        Vault(KVault(get()))
    }
}
