package com.github.tyngstast.borsdatavaluationalarmer

import co.touchlab.kermit.Logger
import co.touchlab.kermit.StaticConfig
import co.touchlab.kermit.platformLogWriter
import com.github.tyngstast.borsdatavaluationalarmer.client.BorsdataClient
import com.github.tyngstast.borsdatavaluationalarmer.client.YahooClient
import com.github.tyngstast.borsdatavaluationalarmer.db.AlarmDao
import com.github.tyngstast.borsdatavaluationalarmer.db.InstrumentDao
import com.github.tyngstast.borsdatavaluationalarmer.db.KpiDao
import com.github.tyngstast.borsdatavaluationalarmer.model.*
import com.github.tyngstast.borsdatavaluationalarmer.settings.AlarmerSettings
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
    single { YahooClient(getWith("YahooClient")) }
    single { AlarmerSettings(get(), get()) }
    single<Clock> { Clock.System }

//    val logger = if (isDebug) platformLogWriter() else CrashlyticsLogWriter()
    val baseLogger = Logger(
        config = StaticConfig(logWriterList = listOf(platformLogWriter())),
        tag = "BorsdataValuationAlarmer"
    )

    factory { (tag: String?) -> if (tag != null) baseLogger.withTag(tag) else baseLogger }

    single {
        AlarmListModel(
            log = getWith("AlarmListModel"),
            instrumentDao = get(),
            kpiDao = get(),
            alarmDao = get(),
            borsdataClient = get(),
            alarmerSettings = get(),
            clock = get()
        )
    }
    single { AddAlarmModel(instrumentDao = get(), kpiDao = get(), alarmDao = get(), appLanguage = get()) }
    single { EditAlarmModel(alarmDao = get()) }
    single { LoginModel(vault = get(), borsdataClient = get()) }
    single {
        ValuationAlarmWorkerModel(
            log = getWith("ValuationAlarmWorkerModel"),
            alarmDao = get(),
            borsdataClient = get(),
            yahooClient = get(),
            alarmerSettings = get(),
            vault = get(),
            clock = get(),
            appLanguage = get()
        )
    }
    single {
        SchedulingModel(alarmerSettings = get(), vault = get())
    }
}

inline fun <reified T> Scope.getWith(vararg params: Any?): T =
    get(parameters = { parametersOf(*params) })

fun KoinComponent.injectLogger(tag: String): Lazy<Logger> = inject { parametersOf(tag) }

expect val platformModule: Module