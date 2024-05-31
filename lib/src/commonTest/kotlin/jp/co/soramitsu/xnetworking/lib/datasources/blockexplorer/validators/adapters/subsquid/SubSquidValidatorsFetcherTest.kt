package jp.co.soramitsu.xnetworking.lib.datasources.blockexplorer.validators.adapters.subsquid

import jp.co.soramitsu.xnetworking.lib.datasources.blockexplorer.api.adapters.ValidatorsFetcher
import jp.co.soramitsu.xnetworking.lib.datasources.blockexplorer.impl.domain.validators.adapters.subsquid.SubSquidValidatorsFetcher
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertContentEquals

class SubSquidValidatorsFetcherTest {

    private val fetcher: ValidatorsFetcher = SubSquidValidatorsFetcher()

    @Test
    fun `TEST subsquidValidatorsFetcher_fetch EXPECT success`() = runTest {
        val result = fetcher.fetch(
            chainId = "polkadot",
            stashAccountAddress = "",
            historicalRange = emptyList()
        )

        assertContentEquals(emptyList(), result)
    }
}