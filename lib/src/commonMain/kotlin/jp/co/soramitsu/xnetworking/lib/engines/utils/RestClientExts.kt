package jp.co.soramitsu.xnetworking.lib.engines.utils

import jp.co.soramitsu.xnetworking.lib.engines.rest.api.RestClient
import kotlinx.serialization.builtins.serializer

suspend fun RestClient.getAsString(
    url: String
): String = getAsString(
    request = JsonGetRequest(
        url = url,
        responseDeserializer = String.serializer()
    )
)

suspend fun RestClient.postAsString(
    url: String,
    body: Any
): String = postAsString(
    request = JsonPostRequest(
        url = url, body = body,
        responseDeserializer = String.serializer()
    )
)