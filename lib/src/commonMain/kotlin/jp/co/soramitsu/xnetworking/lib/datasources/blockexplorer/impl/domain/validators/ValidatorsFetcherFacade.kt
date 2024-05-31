package jp.co.soramitsu.xnetworking.lib.datasources.blockexplorer.impl.domain.validators

import jp.co.soramitsu.xnetworking.lib.datasources.chainsconfig.api.ConfigDAO
import jp.co.soramitsu.xnetworking.lib.datasources.chainsconfig.api.models.ExternalApiType
import jp.co.soramitsu.xnetworking.lib.datasources.blockexplorer.api.adapters.ValidatorsFetcher

class ValidatorsFetcherFacade(
    private val configDAO: ConfigDAO,
    private val fetchersMap: Map<ExternalApiType, ValidatorsFetcher>
): ValidatorsFetcher() {

    override suspend fun fetch(
        chainId: String,
        stashAccountAddress: String,
        historicalRange: List<String>
    ): List<String> {
        val fetcher = checkNotNull(
            fetchersMap[configDAO.stakingType(chainId)]
        ) { "Remote Unbondings Loader could not have been found." }

        return fetcher.fetch(chainId, stashAccountAddress, historicalRange)
    }

}