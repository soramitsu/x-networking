package jp.co.soramitsu.xnetworking.lib.datasources.blockexplorer.apy.adapters.subquery

import io.mockative.Mock
import io.mockative.classOf
import io.mockative.coEvery
import io.mockative.coVerify
import io.mockative.eq
import io.mockative.fake.valueOf
import io.mockative.mock
import jp.co.soramitsu.xnetworking.lib.datasources.chainsconfig.api.ConfigDAO
import jp.co.soramitsu.xnetworking.lib.datasources.chainsconfig.api.models.ExternalApiDAOException
import jp.co.soramitsu.xnetworking.lib.datasources.chainsconfig.api.models.StakingOption
import jp.co.soramitsu.xnetworking.lib.datasources.blockexplorer.api.adapters.ApyFetcher
import jp.co.soramitsu.xnetworking.lib.datasources.blockexplorer.api.models.Apy
import jp.co.soramitsu.xnetworking.lib.datasources.blockexplorer.impl.domain.apy.adapters.subquery.SubQueryApyFetcher
import jp.co.soramitsu.xnetworking.lib.datasources.blockexplorer.impl.domain.apy.adapters.subquery.SubQueryApyRequest
import jp.co.soramitsu.xnetworking.lib.datasources.blockexplorer.impl.domain.apy.adapters.subquery.SubQueryApyResponse
import jp.co.soramitsu.xnetworking.lib.datasources.blockexplorer.impl.domain.apy.adapters.subquery.SubQueryLastRoundRequest
import jp.co.soramitsu.xnetworking.lib.datasources.blockexplorer.impl.domain.apy.adapters.subquery.SubQueryLastRoundResponse
import jp.co.soramitsu.xnetworking.lib.engines.utils.GraphQLResponseDataWrapper
import jp.co.soramitsu.xnetworking.lib.engines.rest.api.RestClient
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertFailsWith

class SubQueryApyFetcherTest {

    private companion object {
        const val chainId = "quartz"
        const val requestUrl = "quartz.url"
    }

    @Mock
    private val configDAO = mock(classOf<ConfigDAO>())

    @Mock
    private val restClient = mock(classOf<RestClient>())

    private val fetcher: ApyFetcher = SubQueryApyFetcher(
        configDAO = configDAO,
        restClient = restClient
    )

    @Test
    fun `TEST subQueryApyFetcher_fetch EXPECT IllegalStateException BECAUSE network staking type is not paraChain`() =
        runTest {
            // Test Data Start
            val selectedCandidates = listOf("0xSomethinig")

            val lastRoundRequestToMock =
                SubQueryLastRoundRequest(
                    url = requestUrl
                )

            val apyRequestToMock =
                SubQueryApyRequest(
                    url = requestUrl,
                    collatorIds = selectedCandidates,
                    roundId = valueOf()
                )
            // Test Data End

            // Mocks Preparation Start
            coEvery {
                configDAO.staking(
                    chainId = chainId
                )
            }.returns(StakingOption.RELAYCHAIN)
            // Mocks Preparation End

            assertFailsWith<IllegalStateException> {
                fetcher.fetch(
                    chainId = chainId,
                    selectedCandidates = selectedCandidates
                )
            }

            // Verification & Assertion
            coVerify {
                restClient.post(
                    request = eq(lastRoundRequestToMock),
                )
            }.wasNotInvoked()

            coVerify {
                restClient.post(
                    request = apyRequestToMock,
                )
            }.wasNotInvoked()
        }

    @Test
    fun `TEST subQueryApyFetcher_fetch EXPECT ExternalApiDAOException_NullUrl BECAUSE staking url is null`() =
        runTest {
            // Test Data Start
            val selectedCandidates = listOf("0xSomethinig")

            val lastRoundRequestToMock =
                SubQueryLastRoundRequest(
                    url = requestUrl
                )

            val apyRequestToMock =
                SubQueryApyRequest(
                    url = requestUrl,
                    collatorIds = selectedCandidates,
                    roundId = valueOf()
                )
            // Test Data End

            // Mocks Preparation Start
            coEvery {
                configDAO.staking(
                    chainId = chainId
                )
            }.returns(StakingOption.PARACHAIN)

            coEvery {
                configDAO.stakingUrl(
                    chainId = chainId
                )
            }.throws(ExternalApiDAOException.NullUrl(chainId))
            // Mocks Preparation End

            assertFailsWith<ExternalApiDAOException.NullUrl> {
                fetcher.fetch(
                    chainId = chainId,
                    selectedCandidates = selectedCandidates
                )
            }

            // Verification & Assertion
            coVerify {
                restClient.post(
                    request = eq(lastRoundRequestToMock),
                )
            }.wasNotInvoked()

            coVerify {
                restClient.post(
                    request = apyRequestToMock,
                )
            }.wasNotInvoked()
        }

    @Test
    fun `TEST subQueryApyFetcher_fetch EXPECT IllegalArgumentException BECAUSE selectedCandidates lack hex prefix`() =
        runTest {
            // Test Data Start
            val selectedCandidates = listOf("")

            val lastRoundRequestToMock =
                SubQueryLastRoundRequest(
                    url = requestUrl
                )

            val apyRequestToMock =
                SubQueryApyRequest(
                    url = requestUrl,
                    collatorIds = selectedCandidates,
                    roundId = valueOf()
                )
            // Test Data End

            // Mocks Preparation Start
            coEvery {
                configDAO.staking(
                    chainId = chainId
                )
            }.returns(StakingOption.PARACHAIN)

            coEvery {
                configDAO.stakingUrl(
                    chainId = chainId
                )
            }.returns(requestUrl)
            // Mocks Preparation End

            assertFailsWith<IllegalArgumentException> {
                fetcher.fetch(
                    chainId = chainId,
                    selectedCandidates = selectedCandidates
                )
            }

            // Verification & Assertion
            coVerify {
                restClient.post(
                    request = eq(lastRoundRequestToMock),
                )
            }.wasNotInvoked()

            coVerify {
                restClient.post(
                    request = apyRequestToMock,
                )
            }.wasNotInvoked()
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

        val apyRequestToMock =
            SubQueryApyRequest(
                url = requestUrl,
                collatorIds = selectedCandidates,
                roundId = lastRoundId.dec()
            )

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

        val expectedResult = listOf(
            Apy(
                id = "collatorId_123",
                value = "apr_123"
            )
        )
        // Test Data End

        // Mocks Preparation Start
        coEvery {
            configDAO.staking(
                chainId = chainId
            )
        }.returns(StakingOption.PARACHAIN)

        coEvery {
            configDAO.stakingUrl(
                chainId = chainId
            )
        }.returns(requestUrl)

        coEvery {
            restClient.post(
                request = eq(lastRoundRequestToMock)
            )
        }.returns(lastRoundResponseToReturn)

        coEvery {
            restClient.post(
                request = eq(apyRequestToMock)
            )
        }.returns(apyResponseToReturn)
        // Mocks Preparation End

        val result = fetcher.fetch(
            chainId = chainId,
            selectedCandidates = selectedCandidates
        )

        // Verification & Assertion
        coVerify {
            restClient.post(
                request = eq(lastRoundRequestToMock)
            )
        }.wasInvoked(1)

        assertContentEquals(
            expectedResult,
            result
        )
    }
}