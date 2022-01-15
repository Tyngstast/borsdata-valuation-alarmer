package com.github.tyngstast.borsdatavaluationalarmer

import io.github.aakira.napier.Napier
import io.ktor.client.*
import io.ktor.client.features.*
import io.ktor.client.features.json.*
import io.ktor.client.features.json.serializer.*
import io.ktor.client.features.logging.*
import io.ktor.client.request.*
import io.ktor.http.*


class BorsdataApi {
    companion object {
        private const val BASE_URL = "https://apiservice.borsdata.se/v1"
        private const val AUTH_PARAM = "authKey"
    }

    private val httpClient = HttpClient {
        install(JsonFeature) {
            val json = kotlinx.serialization.json.Json {
                ignoreUnknownKeys = true
            }
            serializer = KotlinxSerializer(json)
        }
        install(Logging) {
            level = LogLevel.INFO
            logger = object: Logger {
                override fun log(message: String) {
                    Napier.i(tag = "BorsdataAPI", message = message)
                }
            }
        }
        install(HttpTimeout) {
            val timeout = 15000L
            connectTimeoutMillis = timeout
            requestTimeoutMillis = timeout
            socketTimeoutMillis = timeout
        }
    }.also { initLogger() }

    suspend fun getLatestValue(insId: Long, kpiId: Long, authKey: String): InsKpiResponse {
        return httpClient.get {
            insKpi(insId, kpiId)
            parameter(AUTH_PARAM, authKey)
        }
    }

    private fun HttpRequestBuilder.insKpi(insId: Long, kpiId: Long) = url {
        takeFrom(BASE_URL)
        encodedPath += "/instruments/$insId/kpis/$kpiId/last/latest"
    }
}