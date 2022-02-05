package com.github.tyngstast.borsdatavaluationalarmer

import io.github.aakira.napier.Napier
import io.ktor.client.*
import io.ktor.client.features.*
import io.ktor.client.features.json.*
import io.ktor.client.features.json.serializer.*
import io.ktor.client.features.logging.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.serialization.Serializable


class BorsdataApi(private val vault: Vault) {
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
            logger = object : Logger {
                override fun log(message: String) {
                    Napier.i(tag = "BorsdataAPI", message = message)
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
            host = BD_HOST
            url {
                protocol = URLProtocol.HTTPS
            }
            parameter(AUTH_PARAM, vault.getApiKey())
        }
    }.also { initLogger() }

    @Throws(Exception::class)
    suspend fun getLatestValue(insId: Long, kpiId: Long): InsKpiResponse = httpClient.get {
        url {
            encodedPath += "instruments/$insId/kpis/$kpiId/last/latest"
        }
    }

    @Throws(Exception::class)
    suspend fun getInstruments(): List<InstrumentDto> {
        val response = httpClient.get<InstrumentResponse> {
            url {
                encodedPath += "instruments"
            }
        }

        return response.instruments
    }

    @Throws(Exception::class)
    suspend fun getKpis(): List<KpiDto> {
        val response = httpClient.get<KpiResponse> {
            url {
                encodedPath += "instruments/kpis/metadata"
            }
        }

        return response.kpiHistoryMetadatas
    }

    @Throws(Exception::class)
    suspend fun verifyKey(key: String): Boolean {
        try {
            val response: HttpResponse = httpClient.get("instruments/kpis/updated?$AUTH_PARAM=$key")
            return response.status.isSuccess()
        } catch (e : ClientRequestException) {
            if (e.response.status.value == 401) {
                return false;
            }
            throw e
        } catch (e: Throwable) {
            throw e
        }
    }
}

@Serializable
data class InstrumentResponse(val instruments: List<InstrumentDto>)

@Serializable
data class InstrumentDto(
    val insId: Long,
    val name: String,
    val ticker: String,
    val yahoo: String
)

@Serializable
data class KpiResponse(val kpiHistoryMetadatas: List<KpiDto>)

@Serializable
data class KpiDto(
    val kpiId: Long,
    val nameSv: String,
    val format: String? = null
)

@Serializable
data class InsKpiResponse(
    val kpiId: Long,
    val group: String,
    val calculation: String,
    val value: Value
)

@Serializable
data class Value(
    // Instrument ID, echoed back
    val i: Long,
    // Number value when KPI is of that type
    val n: Double,
    // String value when KPI is of that type
    val s: String? = null
)
