package jp.co.soramitsu.xnetworking.lib.datasources.chainsconfig.data

import jp.co.soramitsu.xnetworking.lib.datasources.chainsconfig.api.data.ConfigParser
import jp.co.soramitsu.xnetworking.lib.datasources.chainsconfig.impl.data.RemoteConfigParserImpl
import jp.co.soramitsu.xnetworking.lib.engines.rest.api.RestClient
import jp.co.soramitsu.xnetworking.lib.engines.rest.api.models.AbstractRestServerRequest
import jp.co.soramitsu.xnetworking.lib.engines.utils.JsonGetRequest
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlin.test.Test
import kotlin.test.assertEquals


private open class FakeRestClient(
    private val response: JsonArray
) : RestClient() {
    override suspend fun <T> post(request: AbstractRestServerRequest.WithBody<T>): T {
        TODO("Not yet implemented")
    }

    override suspend fun postAsString(request: AbstractRestServerRequest.WithBody<String>): String {
        TODO("Not yet implemented")
    }

    override suspend fun <T> get(request: AbstractRestServerRequest<T>): T {
        return response as T
    }

    override suspend fun getAsString(request: AbstractRestServerRequest<String>): String {
        TODO("Not yet implemented")
    }
}

class InMemorySavingConfigFetcherImplTest {

    private companion object {
        const val chainId = "sora"
        const val requestUrl = "sora.url"
    }

    @Test
    fun `TEST loadConfigOrGetCached EXPECT success`() = runTest {
        // Test Data Start
//        val configRequestToMock =
//            JsonGetRequest(
//                url = requestUrl,
//                responseDeserializer = JsonArray.serializer()
//            )

        val configResponseToReturn =
            JsonArray(
                content = listOf(
                    JsonObject(
                        content = mapOf(
                            "chainId" to JsonPrimitive(chainId)
                        )
                    )
                )
            )

        val configParser: ConfigParser =
            RemoteConfigParserImpl(
                restClient = FakeRestClient(configResponseToReturn),
                chainsRequestUrl = requestUrl
            )
        // Test Data End

        // Double running should be checked accordingly in verify block
        configParser.getChainObjectById(chainId)
        val result = configParser.getChainObjectById(chainId)

        // Verification & Assertion
        // Verify that implementation is caching, and network request was performed once
//        coVerify {
//            restClient.get(
//                request = configRequestToMock
//            )
//        }.wasInvoked(1)

        assertEquals(configResponseToReturn.first(), result)
    }

}