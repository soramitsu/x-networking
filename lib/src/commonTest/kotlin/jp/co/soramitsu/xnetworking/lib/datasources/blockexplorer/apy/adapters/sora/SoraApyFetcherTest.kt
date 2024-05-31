package jp.co.soramitsu.xnetworking.lib.datasources.blockexplorer.apy.adapters.sora

import io.mockative.Mock
import io.mockative.classOf
import io.mockative.coEvery
import io.mockative.coVerify
import io.mockative.mock
import jp.co.soramitsu.xnetworking.lib.datasources.chainsconfig.api.ConfigDAO
import jp.co.soramitsu.xnetworking.lib.datasources.chainsconfig.api.models.ExternalApiDAOException
import jp.co.soramitsu.xnetworking.lib.datasources.blockexplorer.api.adapters.ApyFetcher
import jp.co.soramitsu.xnetworking.lib.datasources.blockexplorer.impl.domain.apy.adapters.sora.SoraApyFetcher
import jp.co.soramitsu.xnetworking.lib.engines.apollo.api.ApolloClientStore
import jp.co.soramitsu.xnetworking.sorawallet.GetSbApyInfoQuery
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertFailsWith

class SoraApyFetcherTest {

    private companion object {
        const val chainId = "sora"
        const val requestUrl = "sora.url"

        const val cursor = ""
        const val pageCount = 100
    }

    @Mock
    private val apolloClientStore = mock(classOf<ApolloClientStore>())

    @Mock
    private val configDAO = mock(classOf<ConfigDAO>())

    private val fetcher: ApyFetcher =
        SoraApyFetcher(
            apolloClientStore = apolloClientStore,
            configDAO = configDAO
        )

    @Test
    fun `TEST soraApyFetcher_fetch EXPECT ExternalApiDAOException_NullUrl BECAUSE stakingUrl is null`() =
        runTest {
            // Test Data Start
            val selectedCandidates = listOf("0xSomethinig")

            val soraApyRequest =
                GetSbApyInfoQuery(
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
                    chainId = chainId,
                    selectedCandidates = selectedCandidates
                )
            }

            // Validation & Assertion
            coVerify {
                apolloClientStore.query(
                    serverUrl = requestUrl,
                    query = soraApyRequest
                )
            }.wasNotInvoked()
        }
}