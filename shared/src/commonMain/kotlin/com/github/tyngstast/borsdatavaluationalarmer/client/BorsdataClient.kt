package com.github.tyngstast.borsdatavaluationalarmer.client

import com.github.tyngstast.borsdatavaluationalarmer.settings.Vault
import io.ktor.client.features.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import co.touchlab.kermit.Logger as KermitLogger


class BorsdataClient(vault: Vault, log: KermitLogger) :
    Client(BD_HOST, log, AUTH_PARAM to { vault.getApiKey() }) {

    companion object {
        private const val BD_HOST = "apiservice.borsdata.se/v1"
        private const val AUTH_PARAM = "authKey"
    }

    @Throws(Exception::class)
    suspend fun getLatestValue(insId: Long, kpiId: Long): Double {
        val data = httpClient.get<InsKpiResponse> {
            url {
                encodedPath += "instruments/$insId/kpis/$kpiId/last/latest"
            }
        }
        return data.value.n
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
        } catch (e: ClientRequestException) {
            if (e.response.status.value == 401) {
                return false;
            }
            throw e
        } catch (e: Throwable) {
            throw e
        }
    }
}

