package com.github.tyngstast.borsdatavaluationalarmer

import co.touchlab.kermit.Logger
import co.touchlab.kermit.StaticConfig
import co.touchlab.kermit.platformLogWriter
import com.github.tyngstast.borsdatavaluationalarmer.client.BorsdataClient
import com.github.tyngstast.borsdatavaluationalarmer.db.AlarmDao
import com.github.tyngstast.borsdatavaluationalarmer.db.InstrumentDao
import com.github.tyngstast.borsdatavaluationalarmer.db.KpiDao
import kotlinx.coroutines.Dispatchers
import kotlinx.datetime.Clock
import org.koin.core.KoinApplication
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.core.parameter.parametersOf
import org.koin.core.scope.Scope
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
    single { BorsdataClient(get(), getWith("BorsdataClient")) }
    single<Clock> { Clock.System }

    val baseLogger = Logger(
        config = StaticConfig(logWriterList = listOf(platformLogWriter())),
        tag = "BorsdataValuationAlarmer"
    )
    factory { (tag: String?) -> if (tag != null) baseLogger.withTag(tag) else baseLogger }
}

internal inline fun <reified T> Scope.getWith(vararg params: Any?): T =
    get(parameters = { parametersOf(*params) })

fun KoinComponent.injectLogger(tag: String): Lazy<Logger> = inject { parametersOf(tag) }

expect val platformModule: Module