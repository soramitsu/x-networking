package jp.co.soramitsu.xnetworking.lib.engines.rest.impl.builder

import io.ktor.client.HttpClient
import io.ktor.client.call.save
import io.ktor.client.plugins.HttpCallValidator
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.logging.SIMPLE
import io.ktor.client.plugins.websocket.WebSockets
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.serialization.kotlinx.KotlinxWebsocketSerializationConverter
import io.ktor.serialization.kotlinx.json.json
import io.ktor.utils.io.charsets.MalformedInputException
import jp.co.soramitsu.xnetworking.lib.engines.rest.api.models.AbstractRestClientConfig
import jp.co.soramitsu.xnetworking.lib.engines.rest.api.models.RestClientException
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

            /*
                When installing HttpCallValidator, we must silence all the default validators
                set by Ktor internally; thus, expectSuccess is set to False

                On the other hand, if it is set to True, all the responses
                that have status code >= 300 will be handled by default validators,
                prior to our custom one, and they will throw default exceptions
                before we will be able to process them and parse them to our own liking
             */
            expectSuccess = false

            // Installing custom HttpCallValidator
            install(HttpCallValidator) {
                validateResponse { response: HttpResponse ->
                    val statusCode = response.status.value
                    val originCall = response.call

                    if (statusCode < 300) {
                        return@validateResponse
                    }

                    val exceptionCall = originCall.save()

                    val exceptionResponse = exceptionCall.response

                    val exceptionResponseText = try {
                        exceptionResponse.bodyAsText()
                    } catch (_: MalformedInputException) {
                        BODY_FAILED_DECODING
                    }

                    val exception = IllegalStateException("Bad response. Text: \"$exceptionResponseText\"")

                    throw RestClientException.WithCode(
                        code = statusCode,
                        message = exceptionResponseText,
                        error = exception
                    )
                }
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

private const val BODY_FAILED_DECODING: String = "<body failed decoding>"
