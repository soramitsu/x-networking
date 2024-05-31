package jp.co.soramitsu.xnetworking.lib.datasources.blockexplorer.impl.domain.fiat

import jp.co.soramitsu.xnetworking.lib.datasources.blockexplorer.api.models.Fiat
import jp.co.soramitsu.xnetworking.lib.datasources.chainsconfig.api.ConfigDAO
import jp.co.soramitsu.xnetworking.lib.datasources.chainsconfig.api.models.ExternalApiType
import jp.co.soramitsu.xnetworking.lib.datasources.blockexplorer.api.adapters.FiatFetcher

class FiatFetcherFacade(
    private val configDAO: ConfigDAO,
    private val fetchersMap: Map<ExternalApiType, FiatFetcher>
): FiatFetcher() {

    override suspend fun fetch(chainId: String): List<Fiat> {
        val fetcher = checkNotNull(
            fetchersMap[configDAO.stakingType(chainId)]
        ) { "Remote Fiat Loader could not have been found." }

        return fetcher.fetch(chainId)
    }

}