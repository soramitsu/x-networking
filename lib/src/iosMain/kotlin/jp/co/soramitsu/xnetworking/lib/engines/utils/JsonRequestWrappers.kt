package jp.co.soramitsu.xnetworking.lib.engines.utils

import jp.co.soramitsu.xnetworking.lib.engines.rest.api.RestClient
import jp.co.soramitsu.xnetworking.lib.engines.rest.api.models.AbstractRestServerRequest
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.builtins.serializer
import kotlin.experimental.ExperimentalObjCName


@OptIn(ExperimentalObjCName::class)
@ObjCName("JsonGetRequest")
class JsonGetRequestIOS(
    override val url: String,
    override val headers: Map<String, String>? = null,
    override val queryParams: Map<String, String>? = null,
): AbstractRestServerRequest<String>() {
    override val responseDeserializer: DeserializationStrategy<String> = String.serializer()
}

@OptIn(ExperimentalObjCName::class)
@ObjCName("JsonPostRequest")
class JsonPostRequestIOS(
    override val url: String,
    override val body: Any,
): AbstractRestServerRequest.WithBody<String>() {
    override val requestContentType: RestClient.ContentType = RestClient.ContentType.JSON
    override val responseDeserializer: DeserializationStrategy<String> = String.serializer()
}