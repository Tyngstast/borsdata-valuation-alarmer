package com.github.tyngstast.borsdatavaluationalarmer.client

import com.github.tyngstast.borsdatavaluationalarmer.settings.Vault
import io.ktor.client.call.body
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.request.get
import io.ktor.client.statement.HttpResponse
import io.ktor.http.encodedPath
import io.ktor.http.isSuccess
import co.touchlab.kermit.Logger as KermitLogger


class BorsdataClient(vault: Vault, log: KermitLogger) :
    Client(BD_HOST, VERSION, log, AUTH_PARAM to { vault.getApiKey() }) {

    companion object {
        private const val BD_HOST = "apiservice.borsdata.se"
        private const val VERSION = "v1"
        private const val AUTH_PARAM = "authKey"
    }

    @Throws(Exception::class)
    suspend fun getLatestValue(insId: Long, kpiId: Long): Double {
        val data: InsKpiResponse = httpClient.get {
            url {
                encodedPath += "instruments/$insId/kpis/$kpiId/last/latest"
            }
        }.body()

        return data.value.n
    }

    @Throws(Exception::class)
    suspend fun getInstruments(): List<InstrumentDto> {
        val data: InstrumentResponse = httpClient.get("instruments").body()
        return data.instruments
    }

    @Throws(Exception::class)
    suspend fun getKpis(): List<KpiDto> {
        val data: KpiResponse = httpClient.get("instruments/kpis/metadata").body()
        return data.kpiHistoryMetadatas
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

