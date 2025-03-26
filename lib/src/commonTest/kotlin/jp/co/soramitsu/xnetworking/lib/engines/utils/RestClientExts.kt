package jp.co.soramitsu.xnetworking.lib.engines.utils

import jp.co.soramitsu.xnetworking.lib.datasources.txhistory.impl.domain.adapters.subsquid.SubSquidResponse
import jp.co.soramitsu.xnetworking.lib.engines.rest.api.RestClient
import jp.co.soramitsu.xnetworking.lib.engines.rest.api.models.AbstractRestServerRequest
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.builtins.serializer
import kotlin.test.Test

private open class FakeRestClient(
    private val result: String,
) : RestClient() {
    override suspend fun <T> post(request: AbstractRestServerRequest.WithBody<T>): T {
        TODO("Not yet implemented")
    }

    override suspend fun postAsString(request: AbstractRestServerRequest.WithBody<String>): String {
        return result
    }

    override suspend fun <T> get(request: AbstractRestServerRequest<T>): T {
        TODO("Not yet implemented")
    }

    override suspend fun getAsString(request: AbstractRestServerRequest<String>): String {
        return result
    }
}

class RestClientExts {

    @Test
    fun `TEST restClient_getAsString_String EXPECT success`() = runTest {
        val url = "myUrl"
        val result = "myResult"

        val restClientMock = FakeRestClient(result)

        restClientMock.getAsString(
            url = url
        )

//        coVerify {
//            restClientMock.getAsString(
//                request = JsonGetRequest(
//                    url = url,
//                    responseDeserializer = String.serializer()
//                )
//            )
//        }.wasInvoked(1)
    }

    @Test
    fun `TEST restClient_postAsString_String EXPECT success`() = runTest {
        val url = "myUrl"
        val body = "myBody"
        val result = "myResult"

        val restClientMock = FakeRestClient(result)

        restClientMock.postAsString(
            url = url,
            body = body
        )

//        coVerify {
//            restClientMock.postAsString(
//                request = JsonPostRequest(
//                    url = url,
//                    body = body,
//                    responseDeserializer = String.serializer()
//                )
//            )
//        }.wasInvoked(1)
    }

}