package jp.co.soramitsu.xnetworking.basic.engines.rest.api.models

import jp.co.soramitsu.xnetworking.basic.networkclient.WebSocketClientConfig
import kotlinx.serialization.json.Json

abstract class AbstractRestClientConfig {

    abstract class AbstractWebSocketClientConfig: AbstractRestClientConfig() {

        abstract fun webSocketClientConfig(): WebSocketClientConfig

    }

    abstract fun isLoggingEnabled(): Boolean

    abstract fun getConnectTimeoutMillis(): Long

    abstract fun getRequestTimeoutMillis(): Long

    abstract fun getSocketTimeoutMillis(): Long

    abstract fun getOrCreateJsonConfig(): Json

}