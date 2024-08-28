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

class RestClientImpl(
    private val restClientConfig: AbstractRestClientConfig
) : RestClient() {

    private val client by httpClientBuilder { restClientConfig }

    override suspend fun <T> post(
        request: AbstractRestServerRequest.WithBody<T>
    ): T = tryRequestAndDeserialize(request.responseDeserializer) {
        internalPost(request)
    }

    override suspend fun postAsString(
        request: AbstractRestServerRequest.WithBody<String>
    ): String = internalPost(request)

    override suspend fun <T> get(
        request: AbstractRestServerRequest<T>
    ): T = tryRequestAndDeserialize(request.responseDeserializer) {
        internalGet(request)
    }

    override suspend fun getAsString(
        request: AbstractRestServerRequest<String>
    ): String = internalGet(request)

    private suspend fun <T> internalPost(request: AbstractRestServerRequest.WithBody<T>): String =
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

    private suspend fun <T> internalGet(request: AbstractRestServerRequest<T>): String =
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

    private suspend fun <T> tryRequestAndDeserialize(
        deserializer: DeserializationStrategy<T>,
        block: suspend () -> String
    ) = withContext(Dispatchers.Default) {
        try {
            restClientConfig.getOrCreateJsonConfig().decodeFromString(
                deserializer = deserializer,
                string = withContext(Dispatchers.CommonIO) {
                    block.invoke()
                }
            )
        } catch (e: SerializationException) {
            throw RestClientException.WhileSerialization(e.message.orEmpty(), e.cause)
        }
    }

}