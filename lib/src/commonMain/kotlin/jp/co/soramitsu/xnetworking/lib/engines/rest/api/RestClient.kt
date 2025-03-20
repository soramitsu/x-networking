package jp.co.soramitsu.xnetworking.lib.engines.rest.api

import io.mockative.Mockable
import jp.co.soramitsu.xnetworking.lib.engines.rest.api.models.AbstractRestServerRequest
import kotlin.experimental.ExperimentalObjCRefinement
import kotlin.native.HiddenFromObjC

@Mockable
abstract class RestClient {

    enum class ContentType {
        JSON, NONE
    }

    abstract suspend fun <T> post(
        request: AbstractRestServerRequest.WithBody<T>
    ): T

    abstract suspend fun postAsString(
        request: AbstractRestServerRequest.WithBody<String>
    ): String


    abstract suspend fun <T> get(
        request: AbstractRestServerRequest<T>
    ): T

    abstract suspend fun getAsString(
        request: AbstractRestServerRequest<String>
    ): String
}