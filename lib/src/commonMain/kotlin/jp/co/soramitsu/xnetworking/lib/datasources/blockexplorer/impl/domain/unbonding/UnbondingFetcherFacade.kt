package jp.co.soramitsu.xnetworking.lib.datasources.blockexplorer.impl.domain.unbonding

import jp.co.soramitsu.xnetworking.lib.datasources.chainsconfig.api.ConfigDAO
import jp.co.soramitsu.xnetworking.lib.datasources.chainsconfig.api.models.ExternalApiType
import jp.co.soramitsu.xnetworking.lib.datasources.blockexplorer.api.models.Unbonding
import jp.co.soramitsu.xnetworking.lib.datasources.blockexplorer.api.adapters.UnbondingFetcher
import jp.co.soramitsu.xnetworking.lib.engines.utils.CachingFactory
import jp.co.soramitsu.xnetworking.lib.datasources.blockexplorer.impl.domain.unbonding.adapters.subquery.SubQueryUnbondingFetcher
import jp.co.soramitsu.xnetworking.lib.datasources.blockexplorer.impl.domain.unbonding.adapters.subsquid.SubSquidUnbondingFetcher
import jp.co.soramitsu.xnetworking.lib.engines.rest.api.RestClient

class UnbondingFetcherFacade(
    private val configDAO: ConfigDAO,
    private val restClient: RestClient
): UnbondingFetcher() {
    private data class Args(
        val externalApiType: ExternalApiType
    ): CachingFactory.Args()

    private val cachingFactory = CachingFactory<Args, UnbondingFetcher> {
        if (externalApiType === ExternalApiType.SubSquid) {
            return@CachingFactory SubSquidUnbondingFetcher(configDAO, restClient)
        }

        if (externalApiType === ExternalApiType.SubQuery) {
            return@CachingFactory SubQueryUnbondingFetcher(configDAO, restClient)
        }

        error("Remote Unbondings Loader could not have been found.")
    }

    override suspend fun fetch(
        chainId: String,
        delegatorAddress: String,
        collatorAddress: String
    ): List<Unbonding> = cachingFactory.getOrCreate(
        args = Args(externalApiType = configDAO.stakingType(chainId))
    ).fetch(chainId, delegatorAddress, collatorAddress)
}