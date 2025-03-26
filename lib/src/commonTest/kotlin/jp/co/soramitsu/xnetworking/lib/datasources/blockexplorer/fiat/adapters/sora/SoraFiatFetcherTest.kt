package jp.co.soramitsu.xnetworking.lib.datasources.blockexplorer.fiat.adapters.sora

import com.apollographql.apollo.api.Query
import io.ktor.http.hostIsIp
import jp.co.soramitsu.xnetworking.lib.datasources.blockexplorer.api.adapters.FiatFetcher
import jp.co.soramitsu.xnetworking.lib.datasources.blockexplorer.api.models.Fiat
import jp.co.soramitsu.xnetworking.lib.datasources.blockexplorer.impl.domain.fiat.sora.SoraFiatFetcher
import jp.co.soramitsu.xnetworking.lib.datasources.chainsconfig.api.ConfigDAO
import jp.co.soramitsu.xnetworking.lib.datasources.chainsconfig.api.models.ExternalApiDAOException
import jp.co.soramitsu.xnetworking.lib.datasources.chainsconfig.api.models.ExternalApiType
import jp.co.soramitsu.xnetworking.lib.datasources.chainsconfig.api.models.StakingOption
import jp.co.soramitsu.xnetworking.lib.engines.apollo.api.ApolloClientStore
import jp.co.soramitsu.xnetworking.mainnet.GetAssetsInfoQuery
import jp.co.soramitsu.xnetworking.mainnet.GetFiatDataQuery
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertFailsWith

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
    private val fiatResponseToReturn: GetFiatDataQuery.Data?
) : ApolloClientStore() {
    override suspend fun <Response : Query.Data> query(
        serverUrl: String,
        query: Query<Response>
    ): Response {
        return fiatResponseToReturn as Response
    }
}

class SoraFiatFetcherTest {

    private companion object {
        const val chainId = ""
        const val requestUrl = ""

        const val pageCount = 100
        const val cursor = ""
    }

    @Test
    fun `TEST soraFiatFetcher_fetch EXPECT ExternalApiDAOException_NullUrl BECAUSE stakingUrl is null`() =
        runTest {
            // Test Data Start

//            val fiatRequestToMock =
//                GetFiatDataQuery(
//                    pageCount = pageCount,
//                    cursor = cursor
//                )

            val fetcher: FiatFetcher =
            SoraFiatFetcher(
                apolloClientStore = FakeApolloClientStore(null),
                configDAO = FakeConfigDao(null)
            )
            // Test Data End

            assertFailsWith<ExternalApiDAOException.NullUrl> {
                fetcher.fetch(
                    chainId = chainId
                )
            }

            // Validation & Assertion
//            coVerify {
//                apolloClientStore.query(
//                    serverUrl = requestUrl,
//                    query = fiatRequestToMock
//                )
//            }.wasNotInvoked()
        }

    @Test
    fun `TEST soraFiatFetcher_fetch EXPECT success`() =
        runTest {
            // Test Data Start
            val fiatRequestToMock =
                GetFiatDataQuery(
                    pageCount = pageCount,
                    cursor = cursor
                )

            val fiatResponseToReturn =
                GetFiatDataQuery.Data(
                    entities = GetFiatDataQuery.Entities(
                        nodes = listOf(
                            GetFiatDataQuery.Node(
                                id = "id_123",
                                priceUSD = "123"
                            )
                        ),
                        pageInfo = GetFiatDataQuery.PageInfo(
                            hasNextPage = false,
                            endCursor = null
                        )
                    )
                )

            val fetcher: FiatFetcher =
                SoraFiatFetcher(
                    apolloClientStore = FakeApolloClientStore(fiatResponseToReturn),
                    configDAO = FakeConfigDao(requestUrl)
                )

            val expectedResult = listOf(
                Fiat(
                    id = "id_123",
                    priceUSD = "123"
                )
            )
            // Test Data End


            val result = fetcher.fetch(
                chainId = chainId
            )

            // Validation & Assertion
//            coVerify {
//                apolloClientStore.query(
//                    serverUrl = requestUrl,
//                    query = fiatRequestToMock
//                )
//            }.wasInvoked(1)

            assertContentEquals(expectedResult, result)
        }

}