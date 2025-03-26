package jp.co.soramitsu.xnetworking.lib.datasources.blockexplorer.referralreward.adapters.sora


import com.apollographql.apollo.api.Query
import jp.co.soramitsu.xnetworking.lib.datasources.blockexplorer.api.adapters.ReferralRewardFetcher
import jp.co.soramitsu.xnetworking.lib.datasources.blockexplorer.api.models.ReferralReward
import jp.co.soramitsu.xnetworking.lib.datasources.blockexplorer.impl.domain.referralreward.sora.SoraReferralRewardsFetcher
import jp.co.soramitsu.xnetworking.lib.datasources.chainsconfig.api.ConfigDAO
import jp.co.soramitsu.xnetworking.lib.datasources.chainsconfig.api.models.ExternalApiDAOException
import jp.co.soramitsu.xnetworking.lib.datasources.chainsconfig.api.models.ExternalApiType
import jp.co.soramitsu.xnetworking.lib.datasources.chainsconfig.api.models.StakingOption
import jp.co.soramitsu.xnetworking.lib.engines.apollo.api.ApolloClientStore
import jp.co.soramitsu.xnetworking.mainnet.GetReferrerRewardsQuery
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue


private class FakeConfigDao(
    private val historyUrl: String?
) : ConfigDAO() {
    override suspend fun historyType(chainId: String): ExternalApiType {
        TODO("Not yet implemented")
    }

    override suspend fun historyUrl(chainId: String): String {
        return historyUrl ?: throw ExternalApiDAOException.NullUrl(chainId)
    }

    override suspend fun stakingType(chainId: String): ExternalApiType {
        TODO("Not yet implemented")
    }

    override suspend fun stakingUrl(chainId: String): String {
        TODO("Not yet implemented")
    }

    override suspend fun staking(chainId: String): StakingOption? {
        TODO("Not yet implemented")
    }
}

private class FakeApolloClientStore(
    private val fiatResponseToReturn: GetReferrerRewardsQuery.Data?
) : ApolloClientStore() {
    override suspend fun <Response : Query.Data> query(
        serverUrl: String,
        query: Query<Response>
    ): Response {
        return fiatResponseToReturn as Response
    }
}

class SoraReferrerRewardsFetcherTest {

    private companion object {
        const val chainId = ""
        const val requestUrl = ""

        const val pageCount = 100
        const val cursor = ""

        const val address = ""
    }

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

            val fetcher: ReferralRewardFetcher =
                SoraReferralRewardsFetcher(
                    apolloClientStore = FakeApolloClientStore(null),
                    configDAO = FakeConfigDao(null)
                )
            // Test Data End

            assertFailsWith<ExternalApiDAOException.NullUrl> {
                fetcher.fetch(
                    chainId = chainId,
                    address = address
                )
            }

            // Validation & Assertion
//            coVerify {
//                apolloClientStore.query(
//                    serverUrl = requestUrl,
//                    query = referrerRewardsRequest
//                )
//            }.wasNotInvoked()
        }

    @Test
    fun `TEST soraReferralRewardsFetcher_fetch EXPECT success`() =
        runTest {
            // Test Data Start
//            val referralRewardsRequest =
//                GetReferrerRewardsQuery(
//                    pageCount = pageCount,
//                    cursor = cursor,
//                    address = address
//                )

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

            val fetcher: ReferralRewardFetcher =
                SoraReferralRewardsFetcher(
                    apolloClientStore = FakeApolloClientStore(referralRewardsToReturn),
                    configDAO = FakeConfigDao(requestUrl)
                )

            val expectedResult = listOf(
                ReferralReward(
                    referral = "referral_123",
                    amount = "123"
                )
            )
            // Test Data End


            val result = fetcher.fetch(
                chainId = chainId,
                address = address
            )

            // Validation & Assertion
//            coVerify {
//                apolloClientStore.query(
//                    serverUrl = requestUrl,
//                    query = referralRewardsRequest
//                )
//            }.wasInvoked(1)

            assertTrue { result == expectedResult }
        }

}