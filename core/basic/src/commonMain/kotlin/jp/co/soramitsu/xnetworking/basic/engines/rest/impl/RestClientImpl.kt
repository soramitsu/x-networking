package jp.co.soramitsu.xnetworking.basic.engines.rest.impl

import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.RedirectResponseException
import io.ktor.client.plugins.ResponseException
import io.ktor.client.plugins.ServerResponseException
import io.ktor.client.request.accept
import io.ktor.client.request.bearerAuth
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.userAgent
import jp.co.soramitsu.xnetworking.basic.engines.rest.api.RestClient
import jp.co.soramitsu.xnetworking.basic.engines.rest.api.models.AbstractRestClientConfig
import jp.co.soramitsu.xnetworking.basic.engines.rest.api.models.AbstractRestServerRequest
import jp.co.soramitsu.xnetworking.basic.engines.rest.api.models.RestClientException
import jp.co.soramitsu.xnetworking.basic.engines.rest.impl.builder.impl.HttpClientBuilderImpl
import kotlinx.serialization.SerializationException

class RestClientImpl(
    private val restClientConfig: AbstractRestClientConfig
): RestClient {

    private val httpClient = HttpClientBuilderImpl()
        .build(restClientConfig)

    override val config: AbstractRestClientConfig
        get() = restClientConfig

    override suspend fun post(
        request: AbstractRestServerRequest.WithBody
    ): HttpResponse = wrapInExceptionHandler {
        httpClient.post(request.getUrl()) {
            if (request.getRequestBodyContentType() === RestClient.ContentType.JSON)
                contentType(ContentType.Application.Json)

            if (request.getResponseContentType() === RestClient.ContentType.JSON)
                accept(ContentType.Application.Json)

            request.getHeaders()?.filterNot { (key, value) ->
                key.isNotBlank() && value.isNotBlank()
            }?.forEach { (key, value) -> header(key, value) }

            request.getBearerToken().apply {
                if (!this.isNullOrBlank())
                    bearerAuth(this)
            }

            request.getUserAgent().apply {
                if (!this.isNullOrBlank())
                    userAgent(this)
            }

            setBody(request.getBody())
        }
    }

    override suspend fun get(
        request: AbstractRestServerRequest
    ): HttpResponse = wrapInExceptionHandler {
        httpClient.get(request.getUrl()) {
            request.getBearerToken().apply {
                if (!this.isNullOrBlank())
                    bearerAuth(this)
            }

            request.getHeaders()?.filterNot { (key, value) ->
                key.isNotBlank() && value.isNotBlank()
            }?.forEach { (key, value) -> header(key, value) }

            request.getUserAgent().apply {
                if (!this.isNullOrBlank())
                    userAgent(this)
            }

            if (request.getResponseContentType() === RestClient.ContentType.JSON)
                accept(ContentType.Application.Json)
        }
    }

    private suspend fun <Type> wrapInExceptionHandler(
        block: suspend () -> Type
    ): Type = try {
        block.invoke()
    } catch (e: ResponseException) {
        val code: Int = when (e) {
            is RedirectResponseException -> 3
            is ClientRequestException -> 4
            is ServerResponseException -> 5
            else -> 0
        }
        throw RestClientException.WithCode(code, e.message.orEmpty(), e.cause)
    } catch (e: SerializationException) {
        throw RestClientException.WhileSerialization(e.message.orEmpty(), e.cause)
    } catch (e: Throwable) {
        throw RestClientException.SimpleException(e.message.orEmpty(), e.cause)
    }

}