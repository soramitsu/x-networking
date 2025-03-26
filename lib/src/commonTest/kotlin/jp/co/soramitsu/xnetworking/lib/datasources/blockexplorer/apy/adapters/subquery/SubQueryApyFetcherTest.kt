package jp.co.soramitsu.xnetworking.lib.datasources.blockexplorer.apy.adapters.subquery

import jp.co.soramitsu.xnetworking.lib.datasources.blockexplorer.api.adapters.ApyFetcher
import jp.co.soramitsu.xnetworking.lib.datasources.blockexplorer.api.models.Apy
import jp.co.soramitsu.xnetworking.lib.datasources.blockexplorer.impl.domain.apy.adapters.subquery.SubQueryApyFetcher
import jp.co.soramitsu.xnetworking.lib.datasources.blockexplorer.impl.domain.apy.adapters.subquery.SubQueryApyRequest
import jp.co.soramitsu.xnetworking.lib.datasources.blockexplorer.impl.domain.apy.adapters.subquery.SubQueryApyResponse
import jp.co.soramitsu.xnetworking.lib.datasources.blockexplorer.impl.domain.apy.adapters.subquery.SubQueryLastRoundRequest
import jp.co.soramitsu.xnetworking.lib.datasources.blockexplorer.impl.domain.apy.adapters.subquery.SubQueryLastRoundResponse
import jp.co.soramitsu.xnetworking.lib.datasources.chainsconfig.api.ConfigDAO
import jp.co.soramitsu.xnetworking.lib.datasources.chainsconfig.api.models.ExternalApiDAOException
import jp.co.soramitsu.xnetworking.lib.datasources.chainsconfig.api.models.ExternalApiType
import jp.co.soramitsu.xnetworking.lib.datasources.chainsconfig.api.models.StakingOption
import jp.co.soramitsu.xnetworking.lib.engines.rest.api.RestClient
import jp.co.soramitsu.xnetworking.lib.engines.rest.api.models.AbstractRestServerRequest
import jp.co.soramitsu.xnetworking.lib.engines.utils.GraphQLResponseDataWrapper
import jp.co.soramitsu.xnetworking.lib.engines.utils.JsonPostRequest
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
    private val lastRoundRequest: JsonPostRequest<GraphQLResponseDataWrapper<SubQueryLastRoundResponse>>? = null,
    private val lastRoundResponse: GraphQLResponseDataWrapper<SubQueryLastRoundResponse>? = null,
    private val apyResponse: GraphQLResponseDataWrapper<SubQueryApyResponse>? = null,
) : RestClient() {
    override suspend fun <T> post(request: AbstractRestServerRequest.WithBody<T>): T {
        return if (request == lastRoundRequest) {
            lastRoundResponse as T
        } else {
            apyResponse as T
        }
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

class SubQueryApyFetcherTest {

    private companion object {
        const val chainId = "quartz"
        const val requestUrl = "quartz.url"
    }

    @Test
    fun `TEST subQueryApyFetcher_fetch EXPECT IllegalStateException BECAUSE network staking type is not paraChain`() =
        runTest {
            // Test Data Start
            val selectedCandidates = listOf("0xSomethinig")
            val fetcher: ApyFetcher = SubQueryApyFetcher(
                configDAO = FakeConfigDao(StakingOption.RELAYCHAIN, null),
                restClient = FakeRestClient()
            )
//            val lastRoundRequestToMock =
//                SubQueryLastRoundRequest(
//                    url = requestUrl
//                )
//
//            val apyRequestToMock =
//                SubQueryApyRequest(
//                    url = requestUrl,
//                    collatorIds = selectedCandidates,
//                    roundId = valueOf()
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
//                    request = eq(lastRoundRequestToMock),
//                )
//            }.wasNotInvoked()
//
//            coVerify {
//                restClient.post(
//                    request = apyRequestToMock,
//                )
//            }.wasNotInvoked()
        }


    @Test
    fun `TEST subQueryApyFetcher_fetch EXPECT ExternalApiDAOException_NullUrl BECAUSE staking url is null`() =
        runTest {
            // Test Data Start
            val selectedCandidates = listOf("0xSomethinig")

            val fetcher: ApyFetcher = SubQueryApyFetcher(
                configDAO = FakeConfigDao(StakingOption.PARACHAIN, null),
                restClient = FakeRestClient()
            )

//            val lastRoundRequestToMock =
//                SubQueryLastRoundRequest(
//                    url = requestUrl
//                )
//
//            val apyRequestToMock =
//                SubQueryApyRequest(
//                    url = requestUrl,
//                    collatorIds = selectedCandidates,
//                    roundId = valueOf()
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
//                    request = eq(lastRoundRequestToMock),
//                )
//            }.wasNotInvoked()
//
//            coVerify {
//                restClient.post(
//                    request = apyRequestToMock,
//                )
//            }.wasNotInvoked()
        }

    @Test
    fun `TEST subQueryApyFetcher_fetch EXPECT IllegalArgumentException BECAUSE selectedCandidates lack hex prefix`() =
        runTest {
            // Test Data Start
            val selectedCandidates = listOf("")

            val fetcher: ApyFetcher = SubQueryApyFetcher(
                configDAO = FakeConfigDao(StakingOption.PARACHAIN, requestUrl),
                restClient = FakeRestClient()
            )

//            val lastRoundRequestToMock =
//                SubQueryLastRoundRequest(
//                    url = requestUrl
//                )
//
//            val apyRequestToMock =
//                SubQueryApyRequest(
//                    url = requestUrl,
//                    collatorIds = selectedCandidates,
//                    roundId = valueOf()
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
//                    request = eq(lastRoundRequestToMock),
//                )
//            }.wasNotInvoked()
//
//            coVerify {
//                restClient.post(
//                    request = apyRequestToMock,
//                )
//            }.wasNotInvoked()
        }

    @Test
    fun `TEST subQueryApyFetcher_fetch EXPECT success`() = runTest {
        // Test Data Start
        val selectedCandidates = listOf("0xSomethinig")
        val lastRoundId = 123

        val lastRoundRequestToMock =
            SubQueryLastRoundRequest(
                url = requestUrl
            )

        val lastRoundResponseToReturn =
            GraphQLResponseDataWrapper(
                data = SubQueryLastRoundResponse(
                    rounds = SubQueryLastRoundResponse.Rounds(
                        nodes = listOf(
                            SubQueryLastRoundResponse.Rounds.RoundIdElement(
                                id = lastRoundId.toString()
                            )
                        )
                    )
                )
            )
//
//        val apyRequestToMock =
//            SubQueryApyRequest(
//                url = requestUrl,
//                collatorIds = selectedCandidates,
//                roundId = lastRoundId.dec()
//            )

        val apyResponseToReturn =
            GraphQLResponseDataWrapper(
                data = SubQueryApyResponse(
                    collatorRounds = SubQueryApyResponse.CollatorRounds(
                        nodes = listOf(
                            SubQueryApyResponse.CollatorRounds.CollatorApyElement(
                                collatorId = "collatorId_123",
                                apr = "apr_123"
                            )
                        )
                    )
                )
            )

        val restClient = FakeRestClient(
            lastRoundRequest = lastRoundRequestToMock,
            lastRoundResponse = lastRoundResponseToReturn,
            apyResponse = apyResponseToReturn
        )

        val fetcher: ApyFetcher = SubQueryApyFetcher(
            configDAO = FakeConfigDao(StakingOption.PARACHAIN, requestUrl),
            restClient = restClient
        )

        val expectedResult = listOf(
            Apy(
                id = "collatorId_123",
                value = "apr_123"
            )
        )
        // Test Data End

        val result = fetcher.fetch(
            chainId = chainId,
            selectedCandidates = selectedCandidates
        )

        // Verification & Assertion
//        coVerify {
//            restClient.post(
//                request = eq(lastRoundRequestToMock)
//            )
//        }.wasInvoked(1)
//
        assertContentEquals(
            expectedResult,
            result
        )
    }
}