package jp.co.soramitsu.xnetworking.lib.engines.rest.api.models

import kotlinx.serialization.json.Json

abstract class AbstractRestClientConfig {

    abstract class AbstractWebSocketClientConfig: AbstractRestClientConfig() {

        abstract fun getPingInterval(): Long

        abstract fun getMaxFrameSize(): Long

    }

    abstract fun isLoggingEnabled(): Boolean

    abstract fun getConnectTimeoutMillis(): Long

    abstract fun getRequestTimeoutMillis(): Long

    abstract fun getSocketTimeoutMillis(): Long

    abstract fun getOrCreateJsonConfig(): Json

}