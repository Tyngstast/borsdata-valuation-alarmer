package com.github.tyngstast.borsdatavaluationalarmer.client

import co.touchlab.kermit.Logger
import io.ktor.client.request.*

class YahooClient(log: Logger) : Client(YAHOO_HOST, log, *defaultParams) {
    companion object {
        private const val YAHOO_HOST = "query1.finance.yahoo.com/v8"
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
        val data =  httpClient.get<YahooPriceResponse> {
            url {
                encodedPath += "finance/chart/$yahooId"
            }
        }

        return data.chart.result[0].meta.regularMarketPrice
    }
}
