package jp.co.soramitsu.xnetworking.lib.datasources.blockexplorer.fiat.adapters.sora

import io.mockative.Mock
import io.mockative.classOf
import io.mockative.coEvery
import io.mockative.coVerify
import io.mockative.mock
import jp.co.soramitsu.xnetworking.lib.datasources.blockexplorer.api.adapters.FiatFetcher
import jp.co.soramitsu.xnetworking.lib.datasources.blockexplorer.impl.domain.fiat.sora.SoraFiatFetcher
import jp.co.soramitsu.xnetworking.lib.datasources.chainsconfig.api.ConfigDAO
import jp.co.soramitsu.xnetworking.lib.datasources.chainsconfig.api.models.ExternalApiDAOException
import jp.co.soramitsu.xnetworking.lib.datasources.blockexplorer.api.models.Fiat
import jp.co.soramitsu.xnetworking.lib.engines.apollo.api.ApolloClientStore
import jp.co.soramitsu.xnetworking.sorawallet.GetFiatDataQuery
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertFailsWith

class SoraFiatFetcherTest {

    private companion object {
        const val chainId = ""
        const val requestUrl = ""

        const val pageCount = 100
        const val cursor = ""
    }

    @Mock
    private val apolloClientStore = mock(classOf<ApolloClientStore>())

    @Mock
    private val configDAO = mock(classOf<ConfigDAO>())

    private val fetcher: FiatFetcher =
        SoraFiatFetcher(
            apolloClientStore = apolloClientStore,
            configDAO = configDAO
        )

    @Test
    fun `TEST soraFiatFetcher_fetch EXPECT ExternalApiDAOException_NullUrl BECAUSE stakingUrl is null`() =
        runTest {
            // Test Data Start
            val fiatRequestToMock =
                GetFiatDataQuery(
                    pageCount = pageCount,
                    cursor = cursor
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
                    chainId = chainId
                )
            }

            // Validation & Assertion
            coVerify {
                apolloClientStore.query(
                    serverUrl = requestUrl,
                    query = fiatRequestToMock
                )
            }.wasNotInvoked()
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

            val expectedResult = listOf(
                Fiat(
                    id = "id_123",
                    priceUSD = "123"
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
                    query = fiatRequestToMock
                )
            }.returns(fiatResponseToReturn)
            // Mock Preparation End

            val result = fetcher.fetch(
                chainId = chainId
            )

            // Validation & Assertion
            coVerify {
                apolloClientStore.query(
                    serverUrl = requestUrl,
                    query = fiatRequestToMock
                )
            }.wasInvoked(1)

            assertContentEquals(expectedResult, result)
        }

}