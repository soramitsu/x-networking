package jp.co.soramitsu.xnetworking.lib.datasources.blockexplorer.unbonding.adapters.subsquid

import jp.co.soramitsu.xnetworking.lib.datasources.blockexplorer.api.adapters.UnbondingFetcher
import jp.co.soramitsu.xnetworking.lib.datasources.blockexplorer.api.models.Unbonding
import jp.co.soramitsu.xnetworking.lib.datasources.blockexplorer.impl.domain.unbonding.adapters.subsquid.SubSquidUnbondingFetcher
import jp.co.soramitsu.xnetworking.lib.datasources.blockexplorer.impl.domain.unbonding.adapters.subsquid.SubSquidUnbondingRequest
import jp.co.soramitsu.xnetworking.lib.datasources.blockexplorer.impl.domain.unbonding.adapters.subsquid.SubSquidUnbondingResponse
import jp.co.soramitsu.xnetworking.lib.datasources.chainsconfig.api.ConfigDAO
import jp.co.soramitsu.xnetworking.lib.datasources.chainsconfig.api.models.ExternalApiDAOException
import jp.co.soramitsu.xnetworking.lib.datasources.chainsconfig.api.models.ExternalApiType
import jp.co.soramitsu.xnetworking.lib.datasources.chainsconfig.api.models.StakingOption
import jp.co.soramitsu.xnetworking.lib.engines.rest.api.RestClient
import jp.co.soramitsu.xnetworking.lib.engines.rest.api.models.AbstractRestServerRequest
import jp.co.soramitsu.xnetworking.lib.engines.utils.GraphQLResponseDataWrapper
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

private class FakeConfigDao(
    private val stakingOption: StakingOption,
    private val stakingUrl: String?,
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
    private val response: GraphQLResponseDataWrapper<SubSquidUnbondingResponse>? = null
) : RestClient() {
    override suspend fun <T> post(request: AbstractRestServerRequest.WithBody<T>): T {
        return response as T
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

class SubSquidUnbondingFetcherTest {

    private companion object {
        const val chainId = "polkadot"
        const val requestUrl = "polkadot.url"
    }

    @Test
    fun `TEST subSquidUnbondingFetcher_fetch EXPECT IllegalStateException BECAUSE network staking type is not paraChain`() =
        runTest {
            // Test Data Start
            val delegatorAddress = "0xSomething"
            val collatorAddress = "0xSomething"

            val unbondingRequestToMock =
                SubSquidUnbondingRequest(
                    url = requestUrl,
                    delegatorAddress = delegatorAddress,
                    collatorAddress = collatorAddress
                )

            val fetcher: UnbondingFetcher = SubSquidUnbondingFetcher(
                configDAO = FakeConfigDao(StakingOption.RELAYCHAIN, null),
                restClient = FakeRestClient()
            )
            // Test Data End

            assertFailsWith<IllegalStateException> {
                fetcher.fetch(
                    chainId = chainId,
                    delegatorAddress = delegatorAddress,
                    collatorAddress = collatorAddress
                )
            }

            // Verification & Assertion
//            coVerify {
//                restClient.post(
//                    request = eq(unbondingRequestToMock),
//                )
//            }.wasNotInvoked()
        }

    @Test
    fun `TEST subSquidUnbondingFetcher_fetch EXPECT ExternalApiDAOException_NullUrl BECAUSE staking url is null`() =
        runTest {
            // Test Data Start
            val delegatorAddress = "0xSomething"
            val collatorAddress = "0xSomething"

            val unbondingRequestToMock =
                SubSquidUnbondingRequest(
                    url = requestUrl,
                    delegatorAddress = delegatorAddress,
                    collatorAddress = collatorAddress
                )

            val fetcher: UnbondingFetcher = SubSquidUnbondingFetcher(
                configDAO = FakeConfigDao(StakingOption.PARACHAIN, null),
                restClient = FakeRestClient()
            )
            // Test Data End

            assertFailsWith<ExternalApiDAOException.NullUrl> {
                fetcher.fetch(
                    chainId = chainId,
                    delegatorAddress = delegatorAddress,
                    collatorAddress = collatorAddress
                )
            }

            // Verification & Assertion
//            coVerify {
//                restClient.post(
//                    request = eq(unbondingRequestToMock),
//                )
//            }.wasNotInvoked()
        }

    @Test
    fun `TEST subSquidUnbondingFetcher_fetch EXPECT IllegalArgumentException BECAUSE delegatorAddress has no hex prefix`() =
        runTest {
            // Test Data Start
            val delegatorAddress = ""
            val collatorAddress = "0xSomething"

            val unbondingRequestToMock =
                SubSquidUnbondingRequest(
                    url = requestUrl,
                    delegatorAddress = delegatorAddress,
                    collatorAddress = collatorAddress
                )

            val fetcher: UnbondingFetcher = SubSquidUnbondingFetcher(
                configDAO = FakeConfigDao(StakingOption.PARACHAIN, requestUrl),
                restClient = FakeRestClient()
            )
            // Test Data End

            assertFailsWith<IllegalArgumentException> {
                fetcher.fetch(
                    chainId = chainId,
                    delegatorAddress = delegatorAddress,
                    collatorAddress = collatorAddress
                )
            }

            // Verification & Assertion
//            coVerify {
//                restClient.post(
//                    request = eq(unbondingRequestToMock),
//                )
//            }.wasNotInvoked()
        }

    @Test
    fun `TEST subSquidUnbondingFetcher_fetch EXPECT IllegalArgumentException BECAUSE collatorAddress has no hex prefix`() =
        runTest {
            // Test Data Start
            val delegatorAddress = "0xSomething"
            val collatorAddress = ""

            val unbondingRequestToMock =
                SubSquidUnbondingRequest(
                    url = requestUrl,
                    delegatorAddress = delegatorAddress,
                    collatorAddress = collatorAddress
                )

            val fetcher: UnbondingFetcher = SubSquidUnbondingFetcher(
                configDAO = FakeConfigDao(StakingOption.PARACHAIN, requestUrl),
                restClient = FakeRestClient()
            )
            // Test Data End

            assertFailsWith<IllegalArgumentException> {
                fetcher.fetch(
                    chainId = chainId,
                    delegatorAddress = delegatorAddress,
                    collatorAddress = collatorAddress
                )
            }

            // Verification & Assertion
//            coVerify {
//                restClient.post(
//                    request = eq(unbondingRequestToMock),
//                )
//            }.wasNotInvoked()
        }

    @Test
    fun `TEST subSquidUnbondingFetcher_fetch EXPECT success`() = runTest {
        // Test Data Start
        val delegatorAddress = "0xSomething"
        val collatorAddress = "0xSomething"

        val unbondingRequestToMock =
            SubSquidUnbondingRequest(
                url = requestUrl,
                delegatorAddress = delegatorAddress,
                collatorAddress = collatorAddress
            )

        val unbondingResponseToReturn =
            GraphQLResponseDataWrapper(
                data = SubSquidUnbondingResponse(
                    rewards = listOf(
                        SubSquidUnbondingResponse.Reward(
                            id = "id_123",
                            amount = "123",
                            blockNumber = 1,
                            round = 12,
                            timestamp = "timestamp_123",
                            extrinsicHash = "extrinsicHash_123"
                        )
                    )
                )
            )

        val fetcher: UnbondingFetcher = SubSquidUnbondingFetcher(
            configDAO = FakeConfigDao(StakingOption.PARACHAIN, requestUrl),
            restClient = FakeRestClient(unbondingResponseToReturn)
        )
        // Test Data End

        val result = fetcher.fetch(
            chainId = chainId,
            delegatorAddress = delegatorAddress,
            collatorAddress = collatorAddress
        )

        // Verification & Assertion
//        coVerify {
//            restClient.post(
//                request = eq(unbondingRequestToMock),
//            )
//        }.wasInvoked(1)

        assertTrue {
            result.foldRightIndexed(true) { index, unbonding, acc ->
                val expectedUnbonding =
                    unbondingResponseToReturn.data.rewards[index].run {
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

                acc && areElementsTheSame
            }
        }
    }
}