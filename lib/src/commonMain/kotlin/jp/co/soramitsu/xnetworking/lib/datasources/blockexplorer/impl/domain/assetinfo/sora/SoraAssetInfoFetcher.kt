package jp.co.soramitsu.xnetworking.lib.datasources.blockexplorer.impl.domain.assetinfo.sora

import jp.co.soramitsu.xnetworking.lib.datasources.blockexplorer.api.adapters.AssetInfoFetcher
import jp.co.soramitsu.xnetworking.lib.datasources.txhistory.impl.utils.Utils.toDoubleNan
import jp.co.soramitsu.xnetworking.lib.datasources.blockexplorer.api.models.AssetInfo
import jp.co.soramitsu.xnetworking.lib.datasources.chainsconfig.api.ConfigDAO
import jp.co.soramitsu.xnetworking.lib.engines.rest.api.RestClient
import jp.co.soramitsu.xnetworking.lib.engines.utils.fieldOrNull
import jp.co.soramitsu.xnetworking.lib.engines.utils.wrapToGraphQLString

class SoraAssetInfoFetcher(
    private val restClient: RestClient,
    private val configDAO: ConfigDAO
): AssetInfoFetcher() {

    override suspend fun fetch(
        chainId: String,
        tokenIds: List<String>,
        timeStamp: Int
    ): List<AssetInfo> {
        val result = mutableListOf<AssetInfo>()

        var cursor = ""

        while (true) {
            val response = restClient.post(
                request = SoraAssetInfoRequest(
                    url = configDAO.historyUrl(chainId),
                    pageCount = 100,
                    cursor = cursor.wrapToGraphQLString(),
                    tokenIds = tokenIds.map(String::wrapToGraphQLString),
                    timestamp = timeStamp
                )
            ).data.entities

            response.nodes.filterNotNull().forEach { node ->
                node.mapToAssetsInfoResponse()?.let { result.add(it) }
            }

            val (hasNextPage, endCursor) = response.pageInfo.run {
                (hasNextPage ?: false) to endCursor
            }

            if (!hasNextPage || endCursor == null)
                break

            cursor = endCursor
        }

        return result
    }

    private fun SoraAssetInfoResponse.Entities.Node.mapToAssetsInfoResponse(): AssetInfo? {
        return AssetInfo(
            id = id ?: return null,
            liquidity = liquidity ?: return null,
            previousPrice = hourSnapshots?.nodes?.lastOrNull()?.priceUSD.fieldOrNull("open")?.toDoubleNan() ?: return null
        )
    }

}