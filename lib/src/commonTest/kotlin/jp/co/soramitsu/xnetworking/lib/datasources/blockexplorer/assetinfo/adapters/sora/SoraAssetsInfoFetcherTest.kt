package jp.co.soramitsu.xnetworking.lib.datasources.blockexplorer.assetinfo.adapters.sora

import com.apollographql.apollo.api.Query
import jp.co.soramitsu.xnetworking.lib.datasources.blockexplorer.api.adapters.AssetInfoFetcher
import jp.co.soramitsu.xnetworking.lib.datasources.blockexplorer.api.models.AssetInfo
import jp.co.soramitsu.xnetworking.lib.datasources.blockexplorer.impl.domain.assetinfo.sora.SoraAssetInfoFetcher
import jp.co.soramitsu.xnetworking.lib.datasources.chainsconfig.api.ConfigDAO
import jp.co.soramitsu.xnetworking.lib.datasources.chainsconfig.api.models.ExternalApiDAOException
import jp.co.soramitsu.xnetworking.lib.datasources.chainsconfig.api.models.ExternalApiType
import jp.co.soramitsu.xnetworking.lib.datasources.chainsconfig.api.models.StakingOption
import jp.co.soramitsu.xnetworking.lib.engines.apollo.api.ApolloClientStore
import jp.co.soramitsu.xnetworking.mainnet.GetAssetsInfoQuery
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

private class FakeConfigDao(
    private val stakingOption: StakingOption,
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
        return stakingOption
    }
}

private class FakeApolloClientStore(
    private val assetsInfoResponseToReturn: GetAssetsInfoQuery.Data?
) : ApolloClientStore() {
    override suspend fun <Response : Query.Data> query(
        serverUrl: String,
        query: Query<Response>
    ): Response {
        return assetsInfoResponseToReturn as Response
    }
}

class SoraAssetsInfoFetcherTest {

    private companion object {
        const val chainId = ""
        const val requestUrl = ""

        const val pageCount = 100
        const val cursor = ""

        val tokenIds = listOf<String>()
        const val timeStamp = 0
    }

    @Test
    fun `TEST soraAssetInfoFetcher_fetch EXPECT ExternalApiDAOException_NullUrl BECAUSE stakingUrl is null`() =
        runTest {
            // Test Data Start

//            val assetInfoRequestToMock =
//                GetAssetsInfoQuery(
//                    cursor = cursor,
//                    tokenIds = Optional.present(tokenIds),
//                )

            val fetcher: AssetInfoFetcher =
                SoraAssetInfoFetcher(
                    apolloClientStore = FakeApolloClientStore(
                        null
                    ),
                    configDAO = FakeConfigDao(StakingOption.PARACHAIN, null)
                )

            // Test Data End
            assertFailsWith<ExternalApiDAOException.NullUrl> {
                fetcher.fetch(
                    chainId = chainId,
                    tokenIds = tokenIds,
                    timeStamp = timeStamp
                )
            }

            // Validation & Assertion
//            coVerify {
//                apolloClientStore.query(
//                    serverUrl = requestUrl,
//                    query = assetInfoRequestToMock
//                )
//            }.wasNotInvoked()
        }

    @Test
    fun `TEST soraAssetInfoFetcher_fetch EXPECT success`() =
        runTest {
            // Test Data Start
//            val assetInfoRequestToMock =
//                GetAssetsInfoQuery(
//                    cursor = cursor,
//                    tokenIds = Optional.present(tokenIds),
//                )

            val assetsInfoResponseToReturn =
                GetAssetsInfoQuery.Data(
                    data = GetAssetsInfoQuery.Data1(
                        pageInfo = GetAssetsInfoQuery.PageInfo(
                            hasNextPage = false,
                            endCursor = null
                        ),
                        edges = listOf(
                            GetAssetsInfoQuery.Edge(
                                GetAssetsInfoQuery.Node(
                                    id = "id_123",
                                    liquidity = "liquidity_123",
                                    priceChangeDay = "123.0",
                                )
                            )
                        ),
                    ),
                )
            val fetcher: AssetInfoFetcher =
                SoraAssetInfoFetcher(
                    apolloClientStore = FakeApolloClientStore(
                        assetsInfoResponseToReturn
                    ),
                    configDAO = FakeConfigDao(StakingOption.PARACHAIN, requestUrl)
                )

            val expectedResult = listOf(
                AssetInfo(
                    id = "id_123",
                    liquidity = "liquidity_123",
                    previousPrice = 123.0
                )
            )
            // Test Data End

            val result = fetcher.fetch(
                chainId = chainId,
                tokenIds = tokenIds,
                timeStamp = timeStamp
            )

            // Validation & Assertion
//            coVerify {
//                apolloClientStore.query(
//                    serverUrl = requestUrl,
//                    query = assetInfoRequestToMock
//                )
//            }.wasInvoked(1)

            assertTrue { result == expectedResult }
        }

}
