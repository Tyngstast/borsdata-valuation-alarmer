package com.github.tyngstast.borsdatavaluationalarmer.client

import co.touchlab.kermit.Logger
import io.ktor.client.HttpClient
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.http.URLProtocol
import io.ktor.http.path
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import io.ktor.client.plugins.logging.Logger as KtorLogger

abstract class Client(
    baseUrl: String,
    basePath: String,
    log: Logger,
    vararg params: Pair<String, () -> String?>
) {
    val httpClient = HttpClient {
        // Throw exception on non 2xx to keep code compatible with ktor 2.x
        expectSuccess = true
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
            })
        }
        install(Logging) {
            level = LogLevel.INFO
            logger = object : KtorLogger {
                override fun log(message: String) {
                    log.v { message }
                }
            }
        }
        install(HttpTimeout) {
            val timeout = 5000L
            connectTimeoutMillis = timeout
            requestTimeoutMillis = timeout
            socketTimeoutMillis = timeout
        }
        defaultRequest {
            url {
                protocol = URLProtocol.HTTPS
                host = baseUrl
                path("$basePath/")
                params.forEach { (key, getter) ->
                    getter.invoke()?.run {
                        parameters.append(key, this)
                    }
                }
            }
        }
    }
}