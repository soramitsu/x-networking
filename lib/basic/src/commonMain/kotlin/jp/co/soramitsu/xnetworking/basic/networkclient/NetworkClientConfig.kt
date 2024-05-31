package jp.co.soramitsu.xnetworking.basic.networkclient

import kotlinx.serialization.json.Json

data class NetworkClientConfig(
    val logging: Boolean,
    val requestTimeoutMillis: Long,
    val connectTimeoutMillis: Long,
    val socketTimeoutMillis: Long,
    val json: Json,
    val webSocketClientConfig: WebSocketClientConfig?
)

data class WebSocketClientConfig(
    val pingInterval: Long,
    val maxFrameSize: Long
)
