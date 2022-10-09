package com.github.tyngstast.borsdatavaluationalarmer.client

import co.touchlab.kermit.Logger
import io.ktor.client.call.body
import io.ktor.client.request.*
import io.ktor.http.encodedPath

class YahooClient(log: Logger) : Client(YAHOO_HOST, VERSION, log, *defaultParams) {
    companion object {
        private const val YAHOO_HOST = "query1.finance.yahoo.com"
        private const val VERSION = "v8"
        private val defaultParams = arrayOf(
            "region" to { "US" },
            "lang" to { "en-US" },
            "includePrePost" to { "false" },
            "interval" to { "2m" },
            "useYfid" to { "true" },
            "range" to { "1d" },
            "corsDomain" to { "finance.yahoo.com" },
            ".tsrc" to { "finance" },
        )
    }

    suspend fun getLatestPrice(yahooId: String): Double {
        val data: YahooPriceResponse = httpClient.get {
            url {
                encodedPath += "finance/chart/$yahooId"
            }
        }.body()

        return data.chart.result[0].meta.regularMarketPrice
    }
}
