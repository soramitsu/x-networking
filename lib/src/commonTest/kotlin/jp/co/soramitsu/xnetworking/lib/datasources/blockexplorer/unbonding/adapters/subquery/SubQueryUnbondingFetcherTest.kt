package jp.co.soramitsu.xnetworking.lib.datasources.blockexplorer.unbonding.adapters.subquery

import io.mockative.Mock
import io.mockative.classOf
import io.mockative.coEvery
import io.mockative.coVerify
import io.mockative.eq
import io.mockative.mock
import jp.co.soramitsu.xnetworking.lib.datasources.chainsconfig.api.ConfigDAO
import jp.co.soramitsu.xnetworking.lib.datasources.chainsconfig.api.models.ExternalApiDAOException
import jp.co.soramitsu.xnetworking.lib.datasources.chainsconfig.api.models.StakingOption
import jp.co.soramitsu.xnetworking.lib.datasources.blockexplorer.api.adapters.UnbondingFetcher
import jp.co.soramitsu.xnetworking.lib.datasources.blockexplorer.api.models.Unbonding
import jp.co.soramitsu.xnetworking.lib.datasources.blockexplorer.impl.domain.unbonding.adapters.subquery.SubQueryUnbondingFetcher
import jp.co.soramitsu.xnetworking.lib.datasources.blockexplorer.impl.domain.unbonding.adapters.subquery.SubQueryUnbondingRequest
import jp.co.soramitsu.xnetworking.lib.datasources.blockexplorer.impl.domain.unbonding.adapters.subquery.SubQueryUnbondingResponse
import jp.co.soramitsu.xnetworking.lib.engines.utils.GraphQLResponseDataWrapper
import jp.co.soramitsu.xnetworking.lib.engines.rest.api.RestClient
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class SubQueryUnbondingFetcherTest {

    private companion object {
        const val chainId = "quartz"
        const val requestUrl = "quartz.url"
    }

    @Mock
    private val configDAO = mock(classOf<ConfigDAO>())

    @Mock
    private val restClient = mock(classOf<RestClient>())

    private val fetcher: UnbondingFetcher = SubQueryUnbondingFetcher(
        configDAO = configDAO,
        restClient = restClient
    )

    @Test
    fun `TEST subQueryUnbondingFetcher_fetch EXPECT IllegalStateException BECAUSE network staking type is not paraChain`() =
        runTest {
            // Test Data Start
            val delegatorAddress = "0xSomething"
            val collatorAddress = "0xSomething"

            val unbondingRequestToMock =
                SubQueryUnbondingRequest(
                    url = requestUrl,
                    delegatorAddress = delegatorAddress,
                    collatorAddress = collatorAddress
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
                    delegatorAddress = delegatorAddress,
                    collatorAddress = collatorAddress
                )
            }

            // Verification & Assertion
            coVerify {
                restClient.post(
                    request = eq(unbondingRequestToMock),
                )
            }.wasNotInvoked()
        }

    @Test
    fun `TEST subQueryUnbondingFetcher_fetch EXPECT ExternalApiDAOException_NullUrl BECAUSE staking url is null`() =
        runTest {
            // Test Data Start
            val delegatorAddress = "0xSomething"
            val collatorAddress = "0xSomething"

            val unbondingRequestToMock =
                SubQueryUnbondingRequest(
                    url = requestUrl,
                    delegatorAddress = delegatorAddress,
                    collatorAddress = collatorAddress
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
                    delegatorAddress = delegatorAddress,
                    collatorAddress = collatorAddress
                )
            }

            // Verification & Assertion
            coVerify {
                restClient.post(
                    request = eq(unbondingRequestToMock),
                )
            }.wasNotInvoked()
        }

    @Test
    fun `TEST subQueryUnbondingFetcher_fetch EXPECT IllegalArgumentException BECAUSE delegatorAddress has no hex prefix`() =
        runTest {
            // Test Data Start
            val delegatorAddress = ""
            val collatorAddress = "0xSomething"

            val unbondingRequestToMock =
                SubQueryUnbondingRequest(
                    url = requestUrl,
                    delegatorAddress = delegatorAddress,
                    collatorAddress = collatorAddress
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
                    delegatorAddress = delegatorAddress,
                    collatorAddress = collatorAddress
                )
            }

            // Verification & Assertion
            coVerify {
                restClient.post(
                    request = eq(unbondingRequestToMock),
                )
            }.wasNotInvoked()
        }

    @Test
    fun `TEST subQueryUnbondingFetcher_fetch EXPECT IllegalArgumentException BECAUSE collatorAddress has no hex prefix`() =
        runTest {
            // Test Data Start
            val delegatorAddress = "0xSomething"
            val collatorAddress = ""

            val unbondingRequestToMock =
                SubQueryUnbondingRequest(
                    url = requestUrl,
                    delegatorAddress = delegatorAddress,
                    collatorAddress = collatorAddress
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
                    delegatorAddress = delegatorAddress,
                    collatorAddress = collatorAddress
                )
            }

            // Verification & Assertion
            coVerify {
                restClient.post(
                    request = eq(unbondingRequestToMock),
                )
            }.wasNotInvoked()
        }

    @Test
    fun `TEST subQueryUnbondingFetcher_fetch EXPECT success`() = runTest {
        // Test Data Start
        val delegatorAddress = "0xSomething"
        val collatorAddress = "0xSomething"

        val unbondingRequestToMock =
            SubQueryUnbondingRequest(
                url = requestUrl,
                delegatorAddress = delegatorAddress,
                collatorAddress = collatorAddress
            )

        val unbondingResponseToReturn =
            GraphQLResponseDataWrapper(
                data = SubQueryUnbondingResponse(
                    delegatorHistoryElements = SubQueryUnbondingResponse.DelegatorHistoryElements(
                        nodes = listOf(
                            SubQueryUnbondingResponse.DelegatorHistoryElements.HistoryElement(
                                id = "id_123",
                                blockNumber = "blockNumber_123",
                                delegatorId = "delegatorId_123",
                                collatorId = "collatorId_123",
                                timestamp = "123",
                                type = "type",
                                roundId = "roundId_123",
                                amount = "123"
                            )
                        )
                    )
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
                request = eq(unbondingRequestToMock),
            )
        }.returns(unbondingResponseToReturn)
        // Mocks Preparation End

        val result = fetcher.fetch(
            chainId = chainId,
            delegatorAddress = delegatorAddress,
            collatorAddress = collatorAddress
        )

        // Verification & Assertion
        coVerify {
            restClient.post(
                request = eq(unbondingRequestToMock),
            )
        }.wasInvoked(1)

        assertTrue {
            result.foldRightIndexed(true) { index, unbonding, acc ->
                val expectedUnbonding =
                    unbondingResponseToReturn.data.delegatorHistoryElements.nodes[index]
                        .run {
                            Unbonding(
                                amount = amount ?: "0",
                                timestamp = timestamp ?: "0",
                                type = Unbonding.DelegationAction.REWARD
                            )
                        }

                val areElementsTheSame =
                    unbonding.amount == expectedUnbonding.amount &&
                        unbonding.timestamp == expectedUnbonding.timestamp &&
                        unbonding.type == expectedUnbonding.type

                acc &&areElementsTheSame
            }
        }
    }
}