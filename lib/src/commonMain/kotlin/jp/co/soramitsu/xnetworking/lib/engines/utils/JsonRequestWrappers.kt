package jp.co.soramitsu.xnetworking.lib.engines.utils

import jp.co.soramitsu.xnetworking.lib.engines.rest.api.RestClient
import jp.co.soramitsu.xnetworking.lib.engines.rest.api.models.AbstractRestServerRequest
import kotlinx.serialization.DeserializationStrategy
import kotlin.experimental.ExperimentalObjCRefinement
import kotlin.native.HiddenFromObjC

@OptIn(ExperimentalObjCRefinement::class)
@HiddenFromObjC
class JsonPostRequest<T>(
    override val url: String,
    override val body: Any,
    override val responseDeserializer: DeserializationStrategy<T>
): AbstractRestServerRequest.WithBody<T>() {
    override val requestContentType: RestClient.ContentType = RestClient.ContentType.JSON
}

@OptIn(ExperimentalObjCRefinement::class)
@HiddenFromObjC
class JsonGetRequest<Response>(
    override val url: String,
    override val headers: Map<String, String>? = null,
    override val queryParams: Map<String, String>? = null,
    override val responseDeserializer: DeserializationStrategy<Response>
): AbstractRestServerRequest<Response>()