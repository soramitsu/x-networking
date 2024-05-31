package jp.co.soramitsu.xnetworking.lib.datasources.blockexplorer.impl.domain.assetinfo

import jp.co.soramitsu.xnetworking.lib.datasources.blockexplorer.api.models.AssetInfo
import jp.co.soramitsu.xnetworking.lib.datasources.chainsconfig.api.ConfigDAO
import jp.co.soramitsu.xnetworking.lib.datasources.chainsconfig.api.models.ExternalApiType
import jp.co.soramitsu.xnetworking.lib.datasources.blockexplorer.api.adapters.AssetInfoFetcher

class AssetInfoFetcherFacade(
    private val configDAO: ConfigDAO,
    private val fetchersMap: Map<ExternalApiType, AssetInfoFetcher>
): AssetInfoFetcher() {

    override suspend fun fetch(
        chainId: String,
        tokenIds: List<String>,
        timeStamp: Int
    ): List<AssetInfo> {
        val fetcher = checkNotNull(
            fetchersMap[configDAO.stakingType(chainId)]
        ) { "Remote AssetInfo Loader could not have been found." }

        return fetcher.fetch(chainId, tokenIds, timeStamp)
    }

}