package jp.co.soramitsu.xnetworking.lib.datasources.blockexplorer.validators.adapters.subquery

import jp.co.soramitsu.xnetworking.lib.datasources.blockexplorer.api.adapters.ValidatorsFetcher
import jp.co.soramitsu.xnetworking.lib.datasources.blockexplorer.impl.domain.validators.adapters.sora.SoraValidatorsRequest
import jp.co.soramitsu.xnetworking.lib.datasources.blockexplorer.impl.domain.validators.adapters.subquery.SubQueryValidatorsFetcher
import jp.co.soramitsu.xnetworking.lib.datasources.blockexplorer.impl.domain.validators.adapters.subquery.SubQueryValidatorsRequest
import jp.co.soramitsu.xnetworking.lib.datasources.blockexplorer.impl.domain.validators.adapters.subquery.SubQueryValidatorsResponse
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
    private val response: GraphQLResponseDataWrapper<SubQueryValidatorsResponse>? = null
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


class SubQueryValidatorsFetcherTest {

    private companion object {
        const val chainId = "quartz"
        const val requestUrl = "quartz.url"

        const val stashAccountAddress = ""
    }

    @Test
    fun `TEST subQueryValidatorsFetcher_fetch EXPECT IllegalStateException BECAUSE network staking type is not relayChain`() =
        runTest {
            // Test Data Start
            val historicalRange = listOf("from", "to")

            val fetcher: ValidatorsFetcher = SubQueryValidatorsFetcher(
                configDAO = FakeConfigDao(StakingOption.PARACHAIN, null),
                restClient = FakeRestClient()
            )

            val validatorsRequestToMock =
                SoraValidatorsRequest(
                    url = requestUrl,
                    accountAddress = stashAccountAddress,
                    eraFrom = historicalRange.first(),
                    eraTo = historicalRange.last()
                )
            // Test Data End

            assertFailsWith<IllegalStateException> {
                fetcher.fetch(
                    chainId = chainId,
                    stashAccountAddress = stashAccountAddress,
                    historicalRange = historicalRange
                )
            }

            // Verification & Assertion
//            coVerify {
//                restClient.post(
//                    request = eq(validatorsRequestToMock)
//                )
//            }.wasNotInvoked()
        }

    @Test
    fun `TEST subQueryValidatorsFetcher_fetch EXPECT ExternalApiDAOException_NullUrl BECAUSE staking url is null`() =
        runTest {
            // Test Data Start
            val historicalRange = listOf("from", "to")
//
//            val validatorsRequestToMock =
//                SoraValidatorsRequest(
//                    url = requestUrl,
//                    accountAddress = stashAccountAddress,
//                    eraFrom = historicalRange.first(),
//                    eraTo = historicalRange.last()
//                )

            val fetcher: ValidatorsFetcher = SubQueryValidatorsFetcher(
                configDAO = FakeConfigDao(StakingOption.RELAYCHAIN, null),
                restClient = FakeRestClient()
            )
            // Test Data End

            assertFailsWith<ExternalApiDAOException.NullUrl> {
                fetcher.fetch(
                    chainId = chainId,
                    stashAccountAddress = stashAccountAddress,
                    historicalRange = historicalRange
                )
            }

            // Verification & Assertion
//            coVerify {
//                restClient.post(
//                    request = eq(validatorsRequestToMock)
//                )
//            }.wasNotInvoked()
        }

    @Test
    fun `TEST subQueryValidatorsFetcher_fetch EXPECT IllegalArgumentException BECAUSE historical range is empty`() =
        runTest {
            // Test Data Start
            val historicalRange = emptyList<String>()

//            val validatorsRequestToMock =
//                SubQueryValidatorsRequest(
//                    url = requestUrl,
//                    accountAddress = stashAccountAddress,
//                    eraFrom = "should not be accessed",
//                    eraTo = "should not be accessed"
//                )

            val fetcher: ValidatorsFetcher = SubQueryValidatorsFetcher(
                configDAO = FakeConfigDao(StakingOption.RELAYCHAIN, requestUrl),
                restClient = FakeRestClient()
            )
            // Test Data End

            assertFailsWith<IllegalArgumentException> {
                fetcher.fetch(
                    chainId = chainId,
                    stashAccountAddress = stashAccountAddress,
                    historicalRange = historicalRange
                )
            }

            // Verification & Assertion
//            coVerify {
//                restClient.post(
//                    request = eq(validatorsRequestToMock)
//                )
//            }.wasNotInvoked()
        }

    @Test
    fun `TEST subQueryValidatorsFetcher_fetch EXPECT success`() = runTest {
        // Test Data Start
        val historicalRange = listOf("from", "to")

        val validatorsRequestToMock =
            SubQueryValidatorsRequest(
                url = requestUrl,
                accountAddress = stashAccountAddress,
                eraFrom = historicalRange.first(),
                eraTo = historicalRange.last()
            )

        val validatorsResponseToReturn =
            GraphQLResponseDataWrapper(
                data = SubQueryValidatorsResponse(
                    query = SubQueryValidatorsResponse.EraValidatorInfo(
                        eraValidatorInfos = SubQueryValidatorsResponse.EraValidatorInfo.Nodes(
                            nodes = listOf(
                                SubQueryValidatorsResponse.EraValidatorInfo.Nodes.Node(
                                    id = "123",
                                    address = "address",
                                    era = "123",
                                    total = "123",
                                    own = "own"
                                )
                            )
                        )
                    )
                )
            )

        val fetcher: ValidatorsFetcher = SubQueryValidatorsFetcher(
            configDAO = FakeConfigDao(StakingOption.RELAYCHAIN, requestUrl),
            restClient = FakeRestClient(validatorsResponseToReturn)
        )
        // Test Data End

        val result = fetcher.fetch(
            chainId = chainId,
            stashAccountAddress = stashAccountAddress,
            historicalRange = historicalRange
        )

        // Verification & Assertion
//        coVerify {
//            restClient.post(
//                request = eq(validatorsRequestToMock),
//            )
//        }.wasInvoked(1)

        assertContentEquals(
            validatorsResponseToReturn.data.query?.eraValidatorInfos?.nodes
                ?.map { it.address }.orEmpty(),
            result
        )
    }
}