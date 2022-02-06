package com.github.tyngstast.borsdatavaluationalarmer.client

import kotlinx.serialization.Serializable

@Serializable
data class YahooPriceResponse(val chart: Chart)
@Serializable
data class Chart(val result: List<Result>)
@Serializable
data class Result(val meta: Meta)
@Serializable
data class Meta(val regularMarketPrice: Double)

