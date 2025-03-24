//package jp.co.soramitsu.xnetworking.lib.datasources.blockexplorer.assetinfo.adapters.sora
//
//import com.apollographql.apollo.api.Optional
//import io.mockative.coEvery
//import io.mockative.coVerify
//import io.mockative.mock
//import io.mockative.of
//import jp.co.soramitsu.xnetworking.lib.datasources.blockexplorer.api.adapters.AssetInfoFetcher
//import jp.co.soramitsu.xnetworking.lib.datasources.blockexplorer.api.models.AssetInfo
//import jp.co.soramitsu.xnetworking.lib.datasources.blockexplorer.impl.domain.assetinfo.sora.SoraAssetInfoFetcher
//import jp.co.soramitsu.xnetworking.lib.datasources.chainsconfig.api.ConfigDAO
//import jp.co.soramitsu.xnetworking.lib.datasources.chainsconfig.api.models.ExternalApiDAOException
//import jp.co.soramitsu.xnetworking.lib.engines.apollo.api.ApolloClientStore
//import jp.co.soramitsu.xnetworking.mainnet.GetAssetsInfoQuery
//import kotlinx.coroutines.test.runTest
//import kotlin.test.Test
//import kotlin.test.assertFailsWith
//import kotlin.test.assertTrue
//
//class SoraAssetsInfoFetcherTest {
//
//    private companion object {
//        const val chainId = ""
//        const val requestUrl = ""
//
//        const val pageCount = 100
//        const val cursor = ""
//
//        val tokenIds = listOf<String>()
//        const val timeStamp = 0
//    }
//
//    private val apolloClientStore = mock(of<ApolloClientStore>())
//
//    private val configDAO = mock(of<ConfigDAO>())
//
//    private val fetcher: AssetInfoFetcher =
//        SoraAssetInfoFetcher(
//            apolloClientStore = apolloClientStore,
//            configDAO = configDAO
//        )
//
//    @Test
//    fun `TEST soraAssetInfoFetcher_fetch EXPECT ExternalApiDAOException_NullUrl BECAUSE stakingUrl is null`() =
//        runTest {
//            // Test Data Start
//            val assetInfoRequestToMock =
//                GetAssetsInfoQuery(
//                    cursor = cursor,
//                    tokenIds = Optional.present(tokenIds),
//                )
//            // Test Data End
//
//            // Mock Preparation Start
//            coEvery {
//                configDAO.historyUrl(
//                    chainId = chainId
//                )
//            }.throws(ExternalApiDAOException.NullUrl(chainId))
//            // Mock Preparation End
//
//            assertFailsWith<ExternalApiDAOException.NullUrl> {
//                fetcher.fetch(
//                    chainId = chainId,
//                    tokenIds = tokenIds,
//                    timeStamp = timeStamp
//                )
//            }
//
//            // Validation & Assertion
//            coVerify {
//                apolloClientStore.query(
//                    serverUrl = requestUrl,
//                    query = assetInfoRequestToMock
//                )
//            }.wasNotInvoked()
//        }
//
//    @Test
//    fun `TEST soraAssetInfoFetcher_fetch EXPECT success`() =
//        runTest {
//            // Test Data Start
//            val assetInfoRequestToMock =
//                GetAssetsInfoQuery(
//                    cursor = cursor,
//                    tokenIds = Optional.present(tokenIds),
//                )
//
//            val assetsInfoResponseToReturn =
//                GetAssetsInfoQuery.Data(
//                    data = GetAssetsInfoQuery.Data1(
//                        pageInfo = GetAssetsInfoQuery.PageInfo(
//                            hasNextPage = false,
//                            endCursor = null
//                        ),
//                        edges = listOf(
//                            GetAssetsInfoQuery.Edge(
//                                GetAssetsInfoQuery.Node(
//                                    id = "id_123",
//                                    liquidity = "liquidity_123",
//                                    priceChangeDay = "123.0",
//                                )
//                            )
//                        ),
//                    ),
//                )
//
//            val expectedResult = listOf(
//                AssetInfo(
//                    id = "id_123",
//                    liquidity = "liquidity_123",
//                    previousPrice = 123.0
//                )
//            )
//            // Test Data End
//
//            // Mock Preparation Start
//            coEvery {
//                configDAO.historyUrl(
//                    chainId = chainId
//                )
//            }.returns(requestUrl)
//
//            coEvery {
//                apolloClientStore.query(
//                    serverUrl = requestUrl,
//                    query = assetInfoRequestToMock
//                )
//            }.returns(assetsInfoResponseToReturn)
//            // Mock Preparation End
//
//            val result = fetcher.fetch(
//                chainId = chainId,
//                tokenIds = tokenIds,
//                timeStamp = timeStamp
//            )
//
//            // Validation & Assertion
//            coVerify {
//                apolloClientStore.query(
//                    serverUrl = requestUrl,
//                    query = assetInfoRequestToMock
//                )
//            }.wasInvoked(1)
//
//            assertTrue { result == expectedResult }
//        }
//
//}