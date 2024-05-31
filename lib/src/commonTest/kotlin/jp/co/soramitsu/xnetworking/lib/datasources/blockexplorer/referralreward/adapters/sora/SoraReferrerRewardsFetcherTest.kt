package jp.co.soramitsu.xnetworking.lib.datasources.blockexplorer.referralreward.adapters.sora

import io.mockative.Mock
import io.mockative.classOf
import io.mockative.coEvery
import io.mockative.coVerify
import io.mockative.mock
import jp.co.soramitsu.xnetworking.lib.datasources.blockexplorer.api.adapters.ReferralRewardFetcher
import jp.co.soramitsu.xnetworking.lib.datasources.blockexplorer.impl.domain.referralreward.sora.SoraReferralRewardsFetcher
import jp.co.soramitsu.xnetworking.lib.datasources.chainsconfig.api.ConfigDAO
import jp.co.soramitsu.xnetworking.lib.datasources.chainsconfig.api.models.ExternalApiDAOException
import jp.co.soramitsu.xnetworking.lib.datasources.blockexplorer.api.models.ReferralReward
import jp.co.soramitsu.xnetworking.lib.engines.apollo.api.ApolloClientStore
import jp.co.soramitsu.xnetworking.sorawallet.GetReferrerRewardsQuery
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class SoraReferrerRewardsFetcherTest {

    private companion object {
        const val chainId = ""
        const val requestUrl = ""

        const val pageCount = 100
        const val cursor = ""

        const val address = ""
    }

    @Mock
    private val apolloClientStore = mock(classOf<ApolloClientStore>())

    @Mock
    private val configDAO = mock(classOf<ConfigDAO>())

    private val fetcher: ReferralRewardFetcher =
        SoraReferralRewardsFetcher(
            apolloClientStore = apolloClientStore,
            configDAO = configDAO
        )

    @Test
    fun `TEST soraReferralRewardsFetcher_fetch EXPECT ExternalApiDAOException_NullUrl BECAUSE stakingUrl is null`() =
        runTest {
            // Test Data Start
            val referrerRewardsRequest =
                GetReferrerRewardsQuery(
                    pageCount = pageCount,
                    cursor = cursor,
                    address = address
                )
            // Test Data End

            // Mock Preparation Start
            coEvery {
                configDAO.stakingUrl(
                    chainId = chainId
                )
            }.throws(ExternalApiDAOException.NullUrl(chainId))
            // Mock Preparation End

            assertFailsWith<ExternalApiDAOException.NullUrl> {
                fetcher.fetch(
                    chainId = chainId,
                    address = address
                )
            }

            // Validation & Assertion
            coVerify {
                apolloClientStore.query(
                    serverUrl = requestUrl,
                    query = referrerRewardsRequest
                )
            }.wasNotInvoked()
        }

    @Test
    fun `TEST soraReferralRewardsFetcher_fetch EXPECT success`() =
        runTest {
            // Test Data Start
            val referralRewardsRequest =
                GetReferrerRewardsQuery(
                    pageCount = pageCount,
                    cursor = cursor,
                    address = address
                )

            val referralRewardsToReturn =
                GetReferrerRewardsQuery.Data(
                    entities = GetReferrerRewardsQuery.Entities(
                        nodes = listOf(
                            GetReferrerRewardsQuery.Node(
                                referral = "referral_123",
                                amount = "123"
                            )
                        ),
                        pageInfo = GetReferrerRewardsQuery.PageInfo(
                            hasNextPage = false,
                            endCursor = null
                        )
                    )
                )

            val expectedResult = listOf(
                ReferralReward(
                    referral = "referral_123",
                    amount = "123"
                )
            )
            // Test Data End

            // Mock Preparation Start
            coEvery {
                configDAO.stakingUrl(
                    chainId = chainId
                )
            }.returns(requestUrl)

            coEvery {
                apolloClientStore.query(
                    serverUrl = requestUrl,
                    query = referralRewardsRequest
                )
            }.returns(referralRewardsToReturn)
            // Mock Preparation End

            val result = fetcher.fetch(
                chainId = chainId,
                address = address
            )

            // Validation & Assertion
            coVerify {
                apolloClientStore.query(
                    serverUrl = requestUrl,
                    query = referralRewardsRequest
                )
            }.wasInvoked(1)

            assertTrue { result == expectedResult }
        }

}