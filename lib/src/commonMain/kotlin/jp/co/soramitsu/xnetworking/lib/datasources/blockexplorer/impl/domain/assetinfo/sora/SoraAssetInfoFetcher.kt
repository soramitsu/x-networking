package jp.co.soramitsu.xnetworking.lib.datasources.blockexplorer.impl.domain.assetinfo.sora

import com.apollographql.apollo.api.Optional
import jp.co.soramitsu.xnetworking.lib.datasources.blockexplorer.api.adapters.AssetInfoFetcher
import jp.co.soramitsu.xnetworking.lib.datasources.blockexplorer.api.models.AssetInfo
import jp.co.soramitsu.xnetworking.lib.datasources.chainsconfig.api.ConfigDAO
import jp.co.soramitsu.xnetworking.lib.datasources.txhistory.impl.utils.Utils.toDoubleNan
import jp.co.soramitsu.xnetworking.lib.engines.apollo.api.ApolloClientStore
import jp.co.soramitsu.xnetworking.mainnet.GetAssetsInfoQuery

class SoraAssetInfoFetcher(
    private val apolloClientStore: ApolloClientStore,
    private val configDAO: ConfigDAO
) : AssetInfoFetcher() {

    override suspend fun fetch(
        chainId: String,
        tokenIds: List<String>,
        timeStamp: Int
    ): List<AssetInfo> {
        val result = mutableListOf<AssetInfo>()
        var cursor = ""

        while (true) {
            val response = apolloClientStore.query(
                configDAO.historyUrl(chainId),
                GetAssetsInfoQuery(
                    cursor = cursor,
                    tokenIds = Optional.present(tokenIds),
                )
            ).data ?: return emptyList()

            result.addAll(response.edges.mapNotNull { edge ->
                if (edge.node?.id != null && edge.node.liquidity != null) {
                    AssetInfo(
                        edge.node.id,
                        edge.node.liquidity,
                        edge.node.priceChangeDay?.toDoubleNan(),
                    )
                } else {
                    null
                }
            })

            if (response.pageInfo.hasNextPage.not() || response.pageInfo.endCursor == null)
                break

            cursor = response.pageInfo.endCursor
        }

        return result
    }
}