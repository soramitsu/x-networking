package jp.co.soramitsu.xnetworking.lib.datasources.blockexplorer.impl.domain.apy

import jp.co.soramitsu.xnetworking.lib.datasources.chainsconfig.api.ConfigDAO
import jp.co.soramitsu.xnetworking.lib.datasources.chainsconfig.api.models.ExternalApiType
import jp.co.soramitsu.xnetworking.lib.datasources.blockexplorer.api.adapters.ApyFetcher
import jp.co.soramitsu.xnetworking.lib.datasources.blockexplorer.api.models.Apy

class ApyFetcherFacade(
    private val configDAO: ConfigDAO,
    private val fetchersMap: Map<ExternalApiType, ApyFetcher>
): ApyFetcher() {

    override suspend fun fetch(
        chainId: String,
        selectedCandidates: List<String>?
    ): List<Apy> {
        val fetcher = checkNotNull(
            fetchersMap[configDAO.stakingType(chainId)]
        ) { "Remote Apy Loader could not have been found." }

        return fetcher.fetch(chainId, selectedCandidates)
    }

}