package com.github.tyngstast.borsdatavaluationalarmer

import io.github.aakira.napier.Napier
import io.ktor.client.*
import io.ktor.client.features.*
import io.ktor.client.features.json.*
import io.ktor.client.features.json.serializer.*
import io.ktor.client.features.logging.*
import io.ktor.client.request.*
import io.ktor.http.*


class BorsdataApi(kVaultImpl: KVaultImpl) {
    companion object {
        private const val BD_HOST = "apiservice.borsdata.se/v1"
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
        defaultRequest {
            host = BD_HOST
            url {
                protocol = URLProtocol.HTTPS
            }
            parameter(AUTH_PARAM, kVaultImpl.getApiKey())
        }
    }.also { initLogger() }

    @Throws(Exception::class)
    suspend fun getLatestValue(insId: Long, kpiId: Long): InsKpiResponse {
        return httpClient.get { insKpi(insId, kpiId) }
    }

    private fun HttpRequestBuilder.insKpi(insId: Long, kpiId: Long) = url {
        encodedPath += "instruments/$insId/kpis/$kpiId/last/latest"
    }
}