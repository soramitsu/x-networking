package jp.co.soramitsu.xnetworking.lib.datasources.blockexplorer.impl.domain.assetinfo

import jp.co.soramitsu.xnetworking.lib.datasources.blockexplorer.api.models.AssetInfo
import jp.co.soramitsu.xnetworking.lib.datasources.chainsconfig.api.ConfigDAO
import jp.co.soramitsu.xnetworking.lib.datasources.chainsconfig.api.models.ExternalApiType
import jp.co.soramitsu.xnetworking.lib.datasources.blockexplorer.api.adapters.AssetInfoFetcher
import jp.co.soramitsu.xnetworking.lib.datasources.blockexplorer.impl.domain.assetinfo.sora.SoraAssetInfoFetcher
import jp.co.soramitsu.xnetworking.lib.engines.utils.CachingFactory
import jp.co.soramitsu.xnetworking.lib.engines.rest.api.RestClient

class AssetInfoFetcherFacade(
    private val restClient: RestClient,
    private val configDAO: ConfigDAO
): AssetInfoFetcher() {
    private data class Args(
        val externalApiType: ExternalApiType
    ): CachingFactory.Args()

    private val cachingFactory = CachingFactory<Args, AssetInfoFetcher> {
        if (externalApiType === ExternalApiType.Sora)
            return@CachingFactory SoraAssetInfoFetcher(restClient, configDAO)

        error("Remote AssetInfo Loader could not have been found.")
    }

    override suspend fun fetch(
        chainId: String,
        tokenIds: List<String>,
        timeStamp: Int
    ): List<AssetInfo> = cachingFactory.getOrCreate(
        args = Args(externalApiType = configDAO.historyType(chainId))
    ).fetch(chainId, tokenIds, timeStamp)

}