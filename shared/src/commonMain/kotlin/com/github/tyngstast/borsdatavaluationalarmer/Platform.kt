package com.github.tyngstast.borsdatavaluationalarmer

expect class Platform() {
    val platform: String
}

expect fun initLogger();
