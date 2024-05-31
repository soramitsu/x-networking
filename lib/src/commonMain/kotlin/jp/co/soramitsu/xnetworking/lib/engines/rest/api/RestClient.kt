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

    @OptIn(ExperimentalObjCRefinement::class)
    @HiddenFromObjC
    abstract suspend fun <T> get(
        request: AbstractRestServerRequest<T>
    ): T
}