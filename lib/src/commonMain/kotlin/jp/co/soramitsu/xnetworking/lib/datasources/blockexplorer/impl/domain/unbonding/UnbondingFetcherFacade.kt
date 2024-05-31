package jp.co.soramitsu.xnetworking.lib.datasources.blockexplorer.impl.domain.unbonding

import jp.co.soramitsu.xnetworking.lib.datasources.chainsconfig.api.ConfigDAO
import jp.co.soramitsu.xnetworking.lib.datasources.chainsconfig.api.models.ExternalApiType
import jp.co.soramitsu.xnetworking.lib.datasources.blockexplorer.api.models.Unbonding
import jp.co.soramitsu.xnetworking.lib.datasources.blockexplorer.api.adapters.UnbondingFetcher

class UnbondingFetcherFacade(
    private val configDAO: ConfigDAO,
    private val fetchersMap: Map<ExternalApiType, UnbondingFetcher>
): UnbondingFetcher() {

    override suspend fun fetch(
        chainId: String,
        delegatorAddress: String,
        collatorAddress: String
    ): List<Unbonding> {
        val fetcher = checkNotNull(
            fetchersMap[configDAO.stakingType(chainId)]
        ) { "Remote Unbondings Loader could not have been found." }

        return fetcher.fetch(chainId, delegatorAddress, collatorAddress)
    }

}