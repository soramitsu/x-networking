package jp.co.soramitsu.xnetworking.lib.engines.utils

import jp.co.soramitsu.xnetworking.lib.engines.rest.api.RestClient
import jp.co.soramitsu.xnetworking.lib.engines.rest.api.models.AbstractRestServerRequest
import kotlinx.serialization.DeserializationStrategy
import kotlin.experimental.ExperimentalObjCRefinement
import kotlin.native.HiddenFromObjC
import kotlin.reflect.KClass

@Suppress("FunctionName")
@OptIn(ExperimentalObjCRefinement::class)
@HiddenFromObjC
inline fun <reified Response: Any> JsonPostRequest(
    url: String,
    body: Any,
    userAgent: String? = null,
    bearerToken: String? = null,
    headers: Map<String, String>? = null,
    responseDeserializer: DeserializationStrategy<Response>
) = JsonPostRequestNonReified(
    url = url,
    body = body,
    userAgent = userAgent,
    bearerToken = bearerToken,
    headers = headers,
    responseDeserializer = responseDeserializer,
    responseClazz = Response::class
)

@Suppress("FunctionName")
@OptIn(ExperimentalObjCRefinement::class)
@HiddenFromObjC
fun <Response: Any> JsonPostRequestNonReified(
    url: String,
    body: Any,
    userAgent: String? = null,
    bearerToken: String? = null,
    headers: Map<String, String>? = null,
    responseDeserializer: DeserializationStrategy<Response>,
    responseClazz: KClass<Response>
) = object : AbstractRestServerRequest.WithBody<Response>() {
    override val bearerToken: String? = bearerToken

    override val url: String = url

    override val headers: Map<String, String>? = headers

    override val userAgent: String? = userAgent

    override val body: Any = body

    override val responseDeserializer: DeserializationStrategy<Response> = responseDeserializer

    override val responseClazz: KClass<Response> = responseClazz

    override val requestContentType: RestClient.ContentType = RestClient.ContentType.JSON
}

@Suppress("FunctionName")
@OptIn(ExperimentalObjCRefinement::class)
@HiddenFromObjC
inline fun <reified Response: Any> JsonGetRequest(
    url: String,
    userAgent: String? = null,
    bearerToken: String? = null,
    headers: Map<String, String>? = null,
    queryParams: Map<String, String>? = null,
    responseDeserializer: DeserializationStrategy<Response>
) = JsonGetRequestNonReified(
    url = url,
    userAgent = userAgent,
    bearerToken = bearerToken,
    headers = headers,
    queryParams = queryParams,
    responseDeserializer = responseDeserializer,
    responseClazz = Response::class
)

@Suppress("FunctionName")
@OptIn(ExperimentalObjCRefinement::class)
@HiddenFromObjC
fun <Response: Any> JsonGetRequestNonReified(
    url: String,
    userAgent: String? = null,
    bearerToken: String? = null,
    headers: Map<String, String>? = null,
    queryParams: Map<String, String>? = null,
    responseDeserializer: DeserializationStrategy<Response>,
    responseClazz: KClass<Response>
) = object: AbstractRestServerRequest<Response>() {
    override val bearerToken: String? = bearerToken

    override val url: String = url

    override val headers: Map<String, String>? = headers

    override val userAgent: String? = userAgent

    override val queryParams: Map<String, String>? = queryParams

    override val responseDeserializer: DeserializationStrategy<Response> = responseDeserializer

    override val responseClazz: KClass<Response> = responseClazz
}