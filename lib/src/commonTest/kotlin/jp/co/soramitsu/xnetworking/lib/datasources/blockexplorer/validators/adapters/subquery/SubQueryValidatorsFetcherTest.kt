package jp.co.soramitsu.xnetworking.lib.datasources.blockexplorer.validators.adapters.subquery

import io.mockative.Mock
import io.mockative.classOf
import io.mockative.coEvery
import io.mockative.coVerify
import io.mockative.eq
import io.mockative.mock
import jp.co.soramitsu.xnetworking.lib.datasources.chainsconfig.api.ConfigDAO
import jp.co.soramitsu.xnetworking.lib.datasources.chainsconfig.api.models.ExternalApiDAOException
import jp.co.soramitsu.xnetworking.lib.datasources.chainsconfig.api.models.StakingOption
import jp.co.soramitsu.xnetworking.lib.datasources.blockexplorer.api.adapters.ValidatorsFetcher
import jp.co.soramitsu.xnetworking.lib.datasources.blockexplorer.impl.domain.validators.adapters.sora.SoraValidatorsRequest
import jp.co.soramitsu.xnetworking.lib.datasources.blockexplorer.impl.domain.validators.adapters.subquery.SubQueryValidatorsFetcher
import jp.co.soramitsu.xnetworking.lib.datasources.blockexplorer.impl.domain.validators.adapters.subquery.SubQueryValidatorsRequest
import jp.co.soramitsu.xnetworking.lib.datasources.blockexplorer.impl.domain.validators.adapters.subquery.SubQueryValidatorsResponse
import jp.co.soramitsu.xnetworking.lib.engines.utils.GraphQLResponseDataWrapper
import jp.co.soramitsu.xnetworking.lib.engines.rest.api.RestClient
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertFailsWith

class SubQueryValidatorsFetcherTest {

    private companion object {
        const val chainId = "quartz"
        const val requestUrl = "quartz.url"

        const val stashAccountAddress = ""
    }

    @Mock
    private val configDAO = mock(classOf<ConfigDAO>())

    @Mock
    private val restClient = mock(classOf<RestClient>())

    private val fetcher: ValidatorsFetcher = SubQueryValidatorsFetcher(
        configDAO = configDAO,
        restClient = restClient
    )

    @Test
    fun `TEST subQueryValidatorsFetcher_fetch EXPECT IllegalStateException BECAUSE network staking type is not relayChain`() =
        runTest {
            // Test Data Start
            val historicalRange = listOf("from", "to")

            val validatorsRequestToMock =
                SoraValidatorsRequest(
                    url = requestUrl,
                    accountAddress = stashAccountAddress,
                    eraFrom = historicalRange.first(),
                    eraTo = historicalRange.last()
                )
            // Test Data End

            // Mocks Preparation Start
            coEvery {
                configDAO.staking(
                    chainId = chainId
                )
            }.returns(StakingOption.PARACHAIN)
            // Mocks Preparation End

            assertFailsWith<IllegalStateException> {
                fetcher.fetch(
                    chainId = chainId,
                    stashAccountAddress = stashAccountAddress,
                    historicalRange = historicalRange
                )
            }

            // Verification & Assertion
            coVerify {
                restClient.post(
                    request = eq(validatorsRequestToMock)
                )
            }.wasNotInvoked()
        }

    @Test
    fun `TEST subQueryValidatorsFetcher_fetch EXPECT ExternalApiDAOException_NullUrl BECAUSE staking url is null`() =
        runTest {
            // Test Data Start
            val historicalRange = listOf("from", "to")

            val validatorsRequestToMock =
                SoraValidatorsRequest(
                    url = requestUrl,
                    accountAddress = stashAccountAddress,
                    eraFrom = historicalRange.first(),
                    eraTo = historicalRange.last()
                )
            // Test Data End

            // Mocks Preparation Start
            coEvery {
                configDAO.staking(
                    chainId = chainId
                )
            }.returns(StakingOption.RELAYCHAIN)

            coEvery {
                configDAO.stakingUrl(
                    chainId = chainId
                )
            }.throws(ExternalApiDAOException.NullUrl(chainId))
            // Mocks Preparation End

            assertFailsWith<ExternalApiDAOException.NullUrl> {
                fetcher.fetch(
                    chainId = chainId,
                    stashAccountAddress = stashAccountAddress,
                    historicalRange = historicalRange
                )
            }

            // Verification & Assertion
            coVerify {
                restClient.post(
                    request = eq(validatorsRequestToMock)
                )
            }.wasNotInvoked()
        }

    @Test
    fun `TEST subQueryValidatorsFetcher_fetch EXPECT IllegalArgumentException BECAUSE historical range is empty`() =
        runTest {
            // Test Data Start
            val historicalRange = emptyList<String>()

            val validatorsRequestToMock =
                SubQueryValidatorsRequest(
                    url = requestUrl,
                    accountAddress = stashAccountAddress,
                    eraFrom = "should not be accessed",
                    eraTo = "should not be accessed"
                )
            // Test Data End

            // Mocks Preparation Start
            coEvery {
                configDAO.staking(
                    chainId = chainId
                )
            }.returns(StakingOption.RELAYCHAIN)

            coEvery {
                configDAO.stakingUrl(
                    chainId = chainId
                )
            }.returns(requestUrl)
            // Mocks Preparation End

            assertFailsWith<IllegalArgumentException> {
                fetcher.fetch(
                    chainId = chainId,
                    stashAccountAddress = stashAccountAddress,
                    historicalRange = historicalRange
                )
            }

            // Verification & Assertion
            coVerify {
                restClient.post(
                    request = eq(validatorsRequestToMock)
                )
            }.wasNotInvoked()
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
        // Test Data End

        // Mocks Preparation Start
        coEvery {
            configDAO.staking(
                chainId = chainId
            )
        }.returns(StakingOption.RELAYCHAIN)
        
        coEvery {
            configDAO.stakingUrl(
                chainId = chainId
            )
        }.returns(requestUrl)

        coEvery {
            restClient.post(
                request = eq(validatorsRequestToMock),
            )
        }.returns(validatorsResponseToReturn)
        // Mocks Preparation End

        val result = fetcher.fetch(
            chainId = chainId,
            stashAccountAddress = stashAccountAddress,
            historicalRange = historicalRange
        )

        // Verification & Assertion
        coVerify {
            restClient.post(
                request = eq(validatorsRequestToMock),
            )
        }.wasInvoked(1)

        assertContentEquals(
            validatorsResponseToReturn.data.query?.eraValidatorInfos?.nodes
                ?.map { it.address }.orEmpty(),
            result
        )
    }
}