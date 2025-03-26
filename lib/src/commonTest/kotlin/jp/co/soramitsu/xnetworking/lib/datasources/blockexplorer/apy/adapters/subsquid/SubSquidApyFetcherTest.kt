package jp.co.soramitsu.xnetworking.lib.datasources.blockexplorer.apy.adapters.subsquid


import jp.co.soramitsu.xnetworking.lib.datasources.blockexplorer.api.adapters.ApyFetcher
import jp.co.soramitsu.xnetworking.lib.datasources.blockexplorer.api.models.Apy
import jp.co.soramitsu.xnetworking.lib.datasources.blockexplorer.impl.domain.apy.adapters.subquid.SubSquidApyFetcher
import jp.co.soramitsu.xnetworking.lib.datasources.blockexplorer.impl.domain.apy.adapters.subquid.SubSquidApyResponse
import jp.co.soramitsu.xnetworking.lib.datasources.chainsconfig.api.ConfigDAO
import jp.co.soramitsu.xnetworking.lib.datasources.chainsconfig.api.models.ExternalApiDAOException
import jp.co.soramitsu.xnetworking.lib.datasources.chainsconfig.api.models.ExternalApiType
import jp.co.soramitsu.xnetworking.lib.datasources.chainsconfig.api.models.StakingOption
import jp.co.soramitsu.xnetworking.lib.engines.rest.api.RestClient
import jp.co.soramitsu.xnetworking.lib.engines.rest.api.models.AbstractRestServerRequest
import jp.co.soramitsu.xnetworking.lib.engines.utils.GraphQLResponseDataWrapper
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertFailsWith

private class FakeConfigDao(
    private val stakingOption: StakingOption,
    private val stakingUrl: String?
) : ConfigDAO() {
    override suspend fun historyType(chainId: String): ExternalApiType {
        TODO("Not yet implemented")
    }

    override suspend fun historyUrl(chainId: String): String {
        TODO("Not yet implemented")
    }

    override suspend fun stakingType(chainId: String): ExternalApiType {
        TODO("Not yet implemented")
    }

    override suspend fun stakingUrl(chainId: String): String {
        return stakingUrl ?: throw ExternalApiDAOException.NullUrl(chainId)
    }

    override suspend fun staking(chainId: String): StakingOption? {
        return stakingOption
    }
}

private open class FakeRestClient(
    private val apyResponse: GraphQLResponseDataWrapper<SubSquidApyResponse>? = null,
    ) : RestClient() {
    override suspend fun <T> post(request: AbstractRestServerRequest.WithBody<T>): T {
        return apyResponse as T
    }

    override suspend fun postAsString(request: AbstractRestServerRequest.WithBody<String>): String {
        TODO("Not yet implemented")
    }

    override suspend fun <T> get(request: AbstractRestServerRequest<T>): T {
        TODO("Not yet implemented")
    }

    override suspend fun getAsString(request: AbstractRestServerRequest<String>): String {
        TODO("Not yet implemented")
    }
}

class SubSquidApyFetcherTest {

    private companion object {
        const val chainId = "polkadot"
        const val requestUrl = "polkadot.url"
    }

    @Test
    fun `TEST subSquidApyFetcher_fetch EXPECT IllegalStateException BECAUSE network staking type is not paraChain`() =
        runTest {
            // Test Data Start
            val selectedCandidates = listOf("0xSomething")

            val fetcher: ApyFetcher = SubSquidApyFetcher(
                configDAO = FakeConfigDao(StakingOption.RELAYCHAIN, null),
                restClient = FakeRestClient()
            )

//            val apyRequestToMock =
//                SubSquidApyRequest(
//                    url = requestUrl
//                )
            // Test Data End

            assertFailsWith<IllegalStateException> {
                fetcher.fetch(
                    chainId = chainId,
                    selectedCandidates = selectedCandidates
                )
            }

            // Verification & Assertion
//            coVerify {
//                restClient.post(
//                    request = eq(apyRequestToMock),
//                )
//            }.wasNotInvoked()
        }

    @Test
    fun `TEST subSquidApyFetcher_fetch EXPECT ExternalApiDAOException_NullUrl BECAUSE staking url is null`() =
        runTest {
            // Test Data Start
            val selectedCandidates = listOf("0xSomething")

            val fetcher: ApyFetcher = SubSquidApyFetcher(
                configDAO = FakeConfigDao(StakingOption.PARACHAIN, null),
                restClient = FakeRestClient()
            )

//            val apyRequestToMock =
//                SubSquidApyRequest(
//                    url = requestUrl
//                )
            // Test Data End

            assertFailsWith<ExternalApiDAOException.NullUrl> {
                fetcher.fetch(
                    chainId = chainId,
                    selectedCandidates = selectedCandidates
                )
            }

            // Verification & Assertion
//            coVerify {
//                restClient.post(
//                    request = eq(apyRequestToMock),
//                )
//            }.wasNotInvoked()
        }

    @Test
    fun `TEST subSquidApyFetcher_fetch EXPECT IllegalArgumentException BECAUSE selectedCandidates lack hex prefix`() =
        runTest {
            // Test Data Start
            val selectedCandidates = listOf("")

            val fetcher: ApyFetcher = SubSquidApyFetcher(
                configDAO = FakeConfigDao(StakingOption.PARACHAIN, requestUrl),
                restClient = FakeRestClient()
            )

//            val apyRequestToMock =
//                SubSquidApyRequest(
//                    url = requestUrl
//                )
            // Test Data End

            assertFailsWith<IllegalArgumentException> {
                fetcher.fetch(
                    chainId = chainId,
                    selectedCandidates = selectedCandidates
                )
            }

            // Verification & Assertion
//            coVerify {
//                restClient.post(
//                    request = eq(apyRequestToMock),
//                )
//            }.wasNotInvoked()
        }

    @Test
    fun `TEST subSquidApyFetcher_fetch EXPECT success`() = runTest {
        // Test Data Start
        val selectedCandidates = listOf("0xSomething")

//        val apyRequestToMock =
//            SubSquidApyRequest(
//                url = requestUrl
//            )

        val apyResponseToReturn =
            GraphQLResponseDataWrapper(
                data = SubSquidApyResponse(
                    stakers = listOf(
                        SubSquidApyResponse.CollatorApyElement(
                            stashId = "stashId_123",
                            apr24h = "apr_123"
                        )
                    )
                )
            )

        val fetcher: ApyFetcher = SubSquidApyFetcher(
            configDAO = FakeConfigDao(StakingOption.PARACHAIN, requestUrl),
            restClient = FakeRestClient(apyResponseToReturn)
        )

        val expectedResult = listOf(
            Apy("stashId_123", "apr_123")
        )
        // Test Data End

        val result = fetcher.fetch(
            chainId = chainId,
            selectedCandidates = selectedCandidates
        )

        // Verification & Assertion
//        coVerify {
//            restClient.post(
//                request = eq(apyRequestToMock),
//            )
//        }.wasInvoked(1)

        assertContentEquals(expectedResult, result)
    }
}