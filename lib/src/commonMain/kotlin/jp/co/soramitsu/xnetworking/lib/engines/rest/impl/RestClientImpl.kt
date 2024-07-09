package jp.co.soramitsu.xnetworking.lib.engines.rest.impl

import io.ktor.client.request.accept
import io.ktor.client.request.bearerAuth
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.contentType
import io.ktor.http.userAgent
import jp.co.soramitsu.xnetworking.lib.engines.rest.api.RestClient
import jp.co.soramitsu.xnetworking.lib.engines.rest.api.models.AbstractRestClientConfig
import jp.co.soramitsu.xnetworking.lib.engines.rest.api.models.AbstractRestServerRequest
import jp.co.soramitsu.xnetworking.lib.engines.rest.api.models.RestClientException
import jp.co.soramitsu.xnetworking.lib.engines.rest.impl.builder.httpClientBuilder
import jp.co.soramitsu.xnetworking.lib.engines.utils.CommonIO
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.SerializationException
import kotlin.reflect.KClass

class RestClientImpl(
    private val restClientConfig: AbstractRestClientConfig
): RestClient() {

    private val client by httpClientBuilder { restClientConfig }

    override suspend fun <T: Any> post(request: AbstractRestServerRequest.WithBody<T>): T =
        requestCatchingAndDeserialize(
            deserializer = request.responseDeserializer,
            clazz = request.responseClazz
        ) {
            client.post(request.url) {
                if (request.requestContentType === ContentType.JSON)
                    contentType(io.ktor.http.ContentType.Application.Json)

                if (request.responseContentType === ContentType.JSON)
                    accept(io.ktor.http.ContentType.Application.Json)

                request.headers?.filterNot { (key, value) ->
                    key.isBlank() || value.isBlank()
                }?.forEach { (key, value) -> header(key, value) }

                request.bearerToken.apply {
                    if (!this.isNullOrBlank())
                        bearerAuth(this)
                }

                request.userAgent.apply {
                    if (!this.isNullOrBlank())
                        userAgent(this)
                }

                setBody(request.body)
            }.bodyAsText()
        }

    override suspend fun <T: Any> get(request: AbstractRestServerRequest<T>): T =
        requestCatchingAndDeserialize(
            deserializer = request.responseDeserializer,
            clazz = request.responseClazz
        ) {
            client.get(request.url) {
                request.bearerToken.apply {
                    if (!this.isNullOrBlank())
                        bearerAuth(this)
                }

                request.headers?.filterNot { (key, value) ->
                    key.isBlank() || value.isBlank()
                }?.forEach { (key, value) -> header(key, value) }

                request.userAgent.apply {
                    if (!this.isNullOrBlank())
                        userAgent(this)
                }

                if (request.responseContentType === ContentType.JSON)
                    accept(io.ktor.http.ContentType.Application.Json)

                request.queryParams?.forEach { (queryName, queryValue) ->
                    parameter(queryName, queryValue)
                }
            }.bodyAsText()
        }

    private suspend fun <T: Any> requestCatchingAndDeserialize(
        deserializer: DeserializationStrategy<T>,
        clazz: KClass<T>,
        block: suspend () -> String
    ) = withContext(Dispatchers.Default) {
        try {
            val value = withContext(Dispatchers.CommonIO) { block.invoke() }

            if (clazz == String::class) {
                @Suppress("UNCHECKED_CAST")
                return@withContext value as T
            }

            restClientConfig.getOrCreateJsonConfig().decodeFromString(
                deserializer = deserializer,
                string = value
            )
        } catch (e: RestClientException.WithCode) {
            throw e
        } catch (e: SerializationException) {
            throw RestClientException.WhileSerialization(e.message.orEmpty(), e.cause)
        } catch (e: Throwable) {
            throw RestClientException.SimpleException(e.message.orEmpty(), e.cause)
        }
    }

}