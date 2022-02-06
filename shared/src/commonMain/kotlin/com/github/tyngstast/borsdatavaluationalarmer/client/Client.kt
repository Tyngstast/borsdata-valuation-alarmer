package com.github.tyngstast.borsdatavaluationalarmer.client

import co.touchlab.kermit.Logger
import io.ktor.client.*
import io.ktor.client.features.*
import io.ktor.client.features.json.*
import io.ktor.client.features.json.serializer.*
import io.ktor.client.features.logging.*
import io.ktor.client.request.*
import io.ktor.http.*

abstract class Client(
    private val baseUrl: String,
    private val log: Logger,
    vararg params: Pair<String, () -> String?>
) {

    val httpClient = HttpClient {
        install(JsonFeature) {
            val json = kotlinx.serialization.json.Json {
                ignoreUnknownKeys = true
            }
            serializer = KotlinxSerializer(json)
        }
        install(Logging) {
            level = LogLevel.INFO
            logger = object : io.ktor.client.features.logging.Logger {
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
            host = baseUrl
            url {
                protocol = URLProtocol.HTTPS
            }
            params.map { (key, getter) -> parameter(key, getter.invoke()) }
        }
    }
}