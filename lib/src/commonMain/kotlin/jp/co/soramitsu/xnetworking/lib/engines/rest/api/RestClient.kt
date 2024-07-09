package jp.co.soramitsu.xnetworking.lib.engines.rest.api

import jp.co.soramitsu.xnetworking.lib.engines.rest.api.models.AbstractRestServerRequest
import kotlin.experimental.ExperimentalObjCRefinement
import kotlin.native.HiddenFromObjC

abstract class RestClient {

    enum class ContentType {
        JSON, NONE
    }

    @OptIn(ExperimentalObjCRefinement::class)
    @HiddenFromObjC
    abstract suspend fun <T> post(
        request: AbstractRestServerRequest.WithBody<T>
    ): T

    abstract suspend fun postReturnString(
        request: AbstractRestServerRequest.WithBody<String>
    ): String

    @OptIn(ExperimentalObjCRefinement::class)
    @HiddenFromObjC
    abstract suspend fun <T> get(
        request: AbstractRestServerRequest<T>
    ): T

    abstract suspend fun getReturnString(
        request: AbstractRestServerRequest<String>
    ): String
}