package jp.co.soramitsu.xnetworking.basic.engines.rest.api

import io.ktor.client.statement.HttpResponse
import jp.co.soramitsu.xnetworking.basic.engines.rest.api.models.AbstractRestClientConfig
import jp.co.soramitsu.xnetworking.basic.engines.rest.api.models.AbstractRestServerRequest
import jp.co.soramitsu.xnetworking.basic.engines.rest.api.models.RestClientException
import kotlin.coroutines.cancellation.CancellationException

interface RestClient {

    enum class ContentType {
        JSON, NONE
    }

    val config: AbstractRestClientConfig

    @Throws(
        RestClientException::class,
        CancellationException::class
    )
    suspend fun post(
        request: AbstractRestServerRequest.WithBody
    ): HttpResponse

    @Throws(
        RestClientException::class,
        CancellationException::class
    )
    suspend fun get(
        request: AbstractRestServerRequest
    ): HttpResponse
}