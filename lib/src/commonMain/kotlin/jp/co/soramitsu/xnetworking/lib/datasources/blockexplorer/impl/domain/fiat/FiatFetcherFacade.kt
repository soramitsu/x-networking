package jp.co.soramitsu.xnetworking.lib.datasources.blockexplorer.impl.domain.fiat

import jp.co.soramitsu.xnetworking.lib.datasources.blockexplorer.api.models.Fiat
import jp.co.soramitsu.xnetworking.lib.datasources.chainsconfig.api.ConfigDAO
import jp.co.soramitsu.xnetworking.lib.datasources.chainsconfig.api.models.ExternalApiType
import jp.co.soramitsu.xnetworking.lib.datasources.blockexplorer.api.adapters.FiatFetcher
import jp.co.soramitsu.xnetworking.lib.datasources.blockexplorer.impl.domain.fiat.sora.SoraFiatFetcher
import jp.co.soramitsu.xnetworking.lib.engines.utils.CachingFactory
import jp.co.soramitsu.xnetworking.lib.engines.apollo.api.ApolloClientStore

class FiatFetcherFacade(
    private val configDAO: ConfigDAO,
    private val apolloClientStore: ApolloClientStore
): FiatFetcher() {
    private data class Args(
        val externalApiType: ExternalApiType
    ): CachingFactory.Args()

    private val cachingFactory = CachingFactory<Args, FiatFetcher> {
        if (externalApiType === ExternalApiType.Sora)
            return@CachingFactory SoraFiatFetcher(apolloClientStore, configDAO)

        error("Remote Fiat Loader could not have been found.")
    }

    override suspend fun fetch(chainId: String): List<Fiat> =
        cachingFactory.getOrCreate(
            args = Args(externalApiType = configDAO.historyType(chainId))
        ).fetch(chainId)
}