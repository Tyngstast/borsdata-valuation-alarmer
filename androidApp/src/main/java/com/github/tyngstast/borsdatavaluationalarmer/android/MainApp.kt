package com.github.tyngstast.borsdatavaluationalarmer.android

import android.app.Application
import android.content.Context
import com.github.tyngstast.borsdatavaluationalarmer.initKoin
import org.koin.dsl.module

class MainApp : Application() {

    override fun onCreate() {
        super.onCreate()
        initKoin(
            module {
                single<Context> { this@MainApp }
            }
        )
    }
}