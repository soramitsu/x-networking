package jp.co.soramitsu.xnetworking.basic.engines.rest.impl.builder.impl

import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig
import io.ktor.client.engine.HttpClientEngineConfig
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
import jp.co.soramitsu.xnetworking.basic.engines.rest.api.models.AbstractRestClientConfig
import jp.co.soramitsu.xnetworking.basic.engines.rest.impl.builder.HttpClientBuilder

internal class HttpClientBuilderImpl: HttpClientBuilder {

    private fun AbstractRestClientConfig.configureHttpClient():
            HttpClientConfig<HttpClientEngineConfig>.() -> Unit =
        {
            expectSuccess = true

            if (isLoggingEnabled()) {
                install(Logging) {
                    level = LogLevel.ALL
                    logger = Logger.SIMPLE
                }
            }

            install(ContentNegotiation) {
                json(
                    json = getOrCreateJsonConfig(),
                    contentType = ContentType.Any
                )
            }

            install(HttpTimeout) {
                this.requestTimeoutMillis = getRequestTimeoutMillis()
                this.connectTimeoutMillis = getConnectTimeoutMillis()
                this.socketTimeoutMillis = getSocketTimeoutMillis()
            }

            if (this@configureHttpClient is AbstractRestClientConfig.AbstractWebSocketClientConfig) {
                install(WebSockets) {
                    this.pingInterval = webSocketClientConfig().pingInterval
                    this.maxFrameSize = webSocketClientConfig().maxFrameSize
                    this.contentConverter = KotlinxWebsocketSerializationConverter(getOrCreateJsonConfig())
                }
            }
        }

    override fun build(restClientConfig: AbstractRestClientConfig): HttpClient {
        return HttpClient(
            ExpectActualHttpClientEngineFactory().createEngine(),
            restClientConfig.configureHttpClient()
        )
    }

}