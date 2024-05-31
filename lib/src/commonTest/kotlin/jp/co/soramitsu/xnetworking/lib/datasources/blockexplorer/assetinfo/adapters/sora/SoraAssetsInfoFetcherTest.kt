package jp.co.soramitsu.xnetworking.lib.datasources.blockexplorer.assetinfo.adapters.sora

import com.apollographql.apollo3.api.Optional
import io.mockative.Mock
import io.mockative.classOf
import io.mockative.coEvery
import io.mockative.coVerify
import io.mockative.mock
import jp.co.soramitsu.xnetworking.lib.datasources.blockexplorer.api.adapters.AssetInfoFetcher
import jp.co.soramitsu.xnetworking.lib.datasources.blockexplorer.impl.domain.assetinfo.sora.SoraAssetInfoFetcher
import jp.co.soramitsu.xnetworking.lib.datasources.chainsconfig.api.ConfigDAO
import jp.co.soramitsu.xnetworking.lib.datasources.chainsconfig.api.models.ExternalApiDAOException
import jp.co.soramitsu.xnetworking.lib.datasources.blockexplorer.api.models.AssetInfo
import jp.co.soramitsu.xnetworking.lib.engines.apollo.api.ApolloClientStore
import jp.co.soramitsu.xnetworking.sorawallet.GetAssetsInfoQuery
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlin.test.Test
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class SoraAssetsInfoFetcherTest {

    private companion object {
        const val chainId = ""
        const val requestUrl = ""

        const val pageCount = 100
        const val cursor = ""

        val tokenIds = listOf<String>()
        const val timeStamp = 0
    }

    @Mock
    private val apolloClientStore = mock(classOf<ApolloClientStore>())

    @Mock
    private val configDAO = mock(classOf<ConfigDAO>())

    private val fetcher: AssetInfoFetcher =
        SoraAssetInfoFetcher(
            apolloClientStore = apolloClientStore,
            configDAO = configDAO
        )

    @Test
    fun `TEST soraAssetInfoFetcher_fetch EXPECT ExternalApiDAOException_NullUrl BECAUSE stakingUrl is null`() =
        runTest {
            // Test Data Start
            val assetInfoRequestToMock =
                GetAssetsInfoQuery(
                    pageCount = pageCount,
                    cursor = cursor,
                    tokenIds = Optional.present(tokenIds),
                    timestamp = timeStamp
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
                    tokenIds = tokenIds,
                    timeStamp = timeStamp
                )
            }

            // Validation & Assertion
            coVerify {
                apolloClientStore.query(
                    serverUrl = requestUrl,
                    query = assetInfoRequestToMock
                )
            }.wasNotInvoked()
        }

    @Test
    fun `TEST soraAssetInfoFetcher_fetch EXPECT success`() =
        runTest {
            // Test Data Start
            val assetInfoRequestToMock =
                GetAssetsInfoQuery(
                    pageCount = pageCount,
                    cursor = cursor,
                    tokenIds = Optional.present(tokenIds),
                    timestamp = timeStamp
                )

            val assetsInfoResponseToReturn =
                GetAssetsInfoQuery.Data(
                    entities = GetAssetsInfoQuery.Entities(
                        nodes = listOf(
                            GetAssetsInfoQuery.Node(
                                id = "id_123",
                                liquidity = "liquidity_123",
                                hourSnapshots = GetAssetsInfoQuery.HourSnapshots(
                                    nodes = listOf(
                                        GetAssetsInfoQuery.Node1(
                                            priceUSD = JsonObject(
                                                content = mapOf(
                                                    "open" to JsonPrimitive("123")
                                                )
                                            )
                                        )
                                    )
                                )
                            )
                        ),
                        pageInfo = GetAssetsInfoQuery.PageInfo(
                            hasNextPage = false,
                            endCursor = null
                        )
                    )
                )

            val expectedResult = listOf(
                AssetInfo(
                    id = "id_123",
                    liquidity = "liquidity_123",
                    previousPrice = 123.0
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
                    query = assetInfoRequestToMock
                )
            }.returns(assetsInfoResponseToReturn)
            // Mock Preparation End

            val result = fetcher.fetch(
                chainId = chainId,
                tokenIds = tokenIds,
                timeStamp = timeStamp
            )

            // Validation & Assertion
            coVerify {
                apolloClientStore.query(
                    serverUrl = requestUrl,
                    query = assetInfoRequestToMock
                )
            }.wasInvoked(1)

            assertTrue { result == expectedResult }
        }

}