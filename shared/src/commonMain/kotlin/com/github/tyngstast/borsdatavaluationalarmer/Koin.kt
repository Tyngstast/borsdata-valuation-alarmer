package com.github.tyngstast.borsdatavaluationalarmer

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
    single {
        AlarmDao(get())
    }
    single {
        BorsdataApi(get())
    }
}

expect val platformModule: Module