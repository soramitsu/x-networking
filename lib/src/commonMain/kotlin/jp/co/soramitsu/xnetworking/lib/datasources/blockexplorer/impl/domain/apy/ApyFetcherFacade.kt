package jp.co.soramitsu.xnetworking.lib.datasources.blockexplorer.impl.domain.apy

import jp.co.soramitsu.xnetworking.lib.datasources.chainsconfig.api.ConfigDAO
import jp.co.soramitsu.xnetworking.lib.datasources.chainsconfig.api.models.ExternalApiType
import jp.co.soramitsu.xnetworking.lib.datasources.blockexplorer.api.adapters.ApyFetcher
import jp.co.soramitsu.xnetworking.lib.datasources.blockexplorer.api.models.Apy
import jp.co.soramitsu.xnetworking.lib.datasources.blockexplorer.impl.domain.apy.adapters.sora.SoraApyFetcher
import jp.co.soramitsu.xnetworking.lib.datasources.blockexplorer.impl.domain.apy.adapters.subquery.SubQueryApyFetcher
import jp.co.soramitsu.xnetworking.lib.datasources.blockexplorer.impl.domain.apy.adapters.subquid.SubSquidApyFetcher
import jp.co.soramitsu.xnetworking.lib.engines.utils.CachingFactory
import jp.co.soramitsu.xnetworking.lib.engines.apollo.api.ApolloClientStore
import jp.co.soramitsu.xnetworking.lib.engines.rest.api.RestClient

class ApyFetcherFacade(
    private val configDAO: ConfigDAO,
    private val restClient: RestClient,
    private val apolloClientStore: ApolloClientStore
): ApyFetcher() {
    private data class Args(
        val externalApiType: ExternalApiType
    ): CachingFactory.Args()

    private val cachingFactory = CachingFactory<Args, ApyFetcher> {
        if (externalApiType === ExternalApiType.Sora)
            return@CachingFactory SoraApyFetcher(apolloClientStore, configDAO)

        if (externalApiType === ExternalApiType.SubSquid)
            return@CachingFactory SubSquidApyFetcher(configDAO, restClient)

        if (externalApiType === ExternalApiType.SubQuery)
            return@CachingFactory SubQueryApyFetcher(configDAO, restClient)

        error("Remote Apy Loader could not have been found.")
    }

    override suspend fun fetch(chainId: String, selectedCandidates: List<String>?): List<Apy> =
        cachingFactory.getOrCreate(
            args = Args(externalApiType = configDAO.historyType(chainId))
        ).fetch(chainId, selectedCandidates)

}