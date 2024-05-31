package jp.co.soramitsu.xnetworking.lib.engines.rest.impl.builder

import io.ktor.client.HttpClient
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.logging.SIMPLE
import io.ktor.client.plugins.websocket.WebSockets
import io.ktor.http.ContentType
import io.ktor.serialization.kotlinx.KotlinxWebsocketSerializationConverter
import io.ktor.serialization.kotlinx.json.json
import jp.co.soramitsu.xnetworking.lib.engines.rest.api.models.AbstractRestClientConfig
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

internal fun httpClientBuilder(config: () -> AbstractRestClientConfig): ReadOnlyProperty<Any?, HttpClient> =
    HttpClientBuilder(config.invoke())

private class HttpClientBuilder(config: AbstractRestClientConfig) : ReadOnlyProperty<Any?, HttpClient> {

    private val value by lazy {
        HttpClient(ExpectActualHttpClientEngineFactory().createEngine()) {
            expectSuccess = true

            if (config.isLoggingEnabled()) {
                install(Logging) {
                    level = LogLevel.ALL
                    logger = Logger.SIMPLE
                }
            }

            install(ContentNegotiation) {
                json(
                    json = config.getOrCreateJsonConfig(),
                    contentType = ContentType.Any
                )
            }

            install(HttpTimeout) {
                this.requestTimeoutMillis = config.getRequestTimeoutMillis()
                this.connectTimeoutMillis = config.getConnectTimeoutMillis()
                this.socketTimeoutMillis = config.getSocketTimeoutMillis()
            }

            if (config is AbstractRestClientConfig.AbstractWebSocketClientConfig) {
                install(WebSockets) {
                    this.pingInterval = config.getPingInterval()
                    this.maxFrameSize = config.getMaxFrameSize()
                    this.contentConverter =
                        KotlinxWebsocketSerializationConverter(config.getOrCreateJsonConfig())
                }
            }
        }
    }

    override fun getValue(thisRef: Any?, property: KProperty<*>) = value
}