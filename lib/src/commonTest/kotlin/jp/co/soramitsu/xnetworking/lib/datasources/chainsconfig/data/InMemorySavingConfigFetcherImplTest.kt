package jp.co.soramitsu.xnetworking.lib.datasources.chainsconfig.data

import io.mockative.Mock
import io.mockative.classOf
import io.mockative.coEvery
import io.mockative.coVerify
import io.mockative.eq
import io.mockative.mock
import jp.co.soramitsu.xnetworking.lib.datasources.chainsconfig.api.data.ConfigFetcher
import jp.co.soramitsu.xnetworking.lib.datasources.chainsconfig.impl.data.InMemorySavingConfigFetcherImpl
import jp.co.soramitsu.xnetworking.lib.engines.utils.JsonGetRequest
import jp.co.soramitsu.xnetworking.lib.engines.rest.api.RestClient
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlin.test.Test
import kotlin.test.assertEquals

class InMemorySavingConfigFetcherImplTest {

    private companion object {
        const val chainId = "sora"
        const val requestUrl = "sora.url"
    }

    @Mock
    private val restClient = mock(classOf<RestClient>())


    private val configFetcher: ConfigFetcher =
        InMemorySavingConfigFetcherImpl(
            restClient = restClient,
            chainsRequestUrl = requestUrl
        )

    @Test
    fun `TEST loadConfigOrGetCached EXPECT success`() = runTest {
        // Test Data Start
        val configRequestToMock =
            JsonGetRequest(
                url = requestUrl,
                responseDeserializer = JsonArray.serializer()
            )

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
        // Test Data End

        // Mocks Preparation Start
        coEvery {
            restClient.get(
                request = eq(configRequestToMock)
            )
        }.returns(configResponseToReturn)
        // Mocks Preparation End

        // Double running should be checked accordingly in verify block
        configFetcher.fetch(chainId)
        val result = configFetcher.fetch(chainId)

        // Verification & Assertion
        // Verify that implementation is caching, and network request was performed once
        coVerify {
            restClient.get(
                request = configRequestToMock
            )
        }.wasInvoked(1)

        assertEquals(configResponseToReturn.first(), result)
    }

}