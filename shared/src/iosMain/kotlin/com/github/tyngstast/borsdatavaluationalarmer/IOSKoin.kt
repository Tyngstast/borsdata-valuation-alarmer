package com.github.tyngstast.borsdatavaluationalarmer

import com.github.tyngstast.db.ValueAlarmerDb
import com.liftric.kvault.KVault
import com.squareup.sqldelight.db.SqlDriver
import com.squareup.sqldelight.drivers.native.NativeSqliteDriver
import org.koin.dsl.module

actual val platformModule = module {
    single<SqlDriver> {
        NativeSqliteDriver(ValueAlarmerDb.Schema, "ValueAlarmerDb")
    }
    single {
        Vault(KVault(null, null))
    }
}
