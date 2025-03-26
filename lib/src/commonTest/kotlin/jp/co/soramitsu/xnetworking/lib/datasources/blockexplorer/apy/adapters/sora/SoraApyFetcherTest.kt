package jp.co.soramitsu.xnetworking.lib.datasources.blockexplorer.apy.adapters.sora

import jp.co.soramitsu.xnetworking.lib.datasources.blockexplorer.api.adapters.ApyFetcher
import jp.co.soramitsu.xnetworking.lib.datasources.blockexplorer.impl.domain.apy.adapters.sora.SoraApyFetcher
import jp.co.soramitsu.xnetworking.lib.datasources.chainsconfig.api.ConfigDAO
import jp.co.soramitsu.xnetworking.lib.datasources.chainsconfig.api.models.ExternalApiDAOException
import jp.co.soramitsu.xnetworking.lib.datasources.chainsconfig.api.models.ExternalApiType
import jp.co.soramitsu.xnetworking.lib.datasources.chainsconfig.api.models.StakingOption
import jp.co.soramitsu.xnetworking.lib.engines.apollo.impl.ApolloClientStoreImpl
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertFailsWith


private class FakeConfigDao : ConfigDAO() {
    override suspend fun historyType(chainId: String): ExternalApiType {
        TODO("Not yet implemented")
    }

    override suspend fun historyUrl(chainId: String): String {
        throw ExternalApiDAOException.NullUrl(chainId)
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

class SoraApyFetcherTest {

    private companion object {
        const val chainId = "sora"
        const val requestUrl = "sora.url"

        const val cursor = ""
        const val pageCount = 100
    }

    private val apolloClientStore = ApolloClientStoreImpl()

    private val configDAO = FakeConfigDao()

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

//            val soraApyRequest =
//                GetSbApyInfoQuery(
//                    cursor = cursor
//                )
            // Test Data End

            assertFailsWith<ExternalApiDAOException.NullUrl> {
                fetcher.fetch(
                    chainId = chainId,
                    selectedCandidates = selectedCandidates
                )
            }

            // Validation & Assertion
//            coVerify {
//                apolloClientStore.query(
//                    serverUrl = requestUrl,
//                    query = soraApyRequest
//                )
//            }.wasNotInvoked()
        }
}