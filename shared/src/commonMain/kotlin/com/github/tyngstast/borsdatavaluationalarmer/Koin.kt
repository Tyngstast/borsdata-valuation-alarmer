package com.github.tyngstast.borsdatavaluationalarmer

import com.github.tyngstast.borsdatavaluationalarmer.db.AlarmDao
import com.github.tyngstast.borsdatavaluationalarmer.db.InstrumentDao
import com.github.tyngstast.borsdatavaluationalarmer.db.KpiDao
import kotlinx.coroutines.Dispatchers
import kotlinx.datetime.Clock
import org.koin.core.KoinApplication
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.dsl.module


fun initKoin(appModule: Module): KoinApplication = startKoin {
    modules(
        appModule,
        platformModule,
        coreModule
    )
}

val coreModule = module {
    single { AlarmDao(get(), Dispatchers.Default) }
    single { InstrumentDao(get(), Dispatchers.Default) }
    single { KpiDao(get(), Dispatchers.Default) }
    single { BorsdataApi(get()) }
    single<Clock> { Clock.System }
}

expect val platformModule: Module