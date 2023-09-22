package jp.co.soramitsu.xnetworking.sorawallet.core.datasources.blockexplorer.impl.rest.assets

import io.ktor.http.HttpMethod
import jp.co.soramitsu.xnetworking.basic.common.Utils.toDoubleNan
import jp.co.soramitsu.xnetworking.basic.datasources.txhistory.impl.old.subquery.graphqlrequest.SubQueryRequest
import jp.co.soramitsu.xnetworking.basic.networkclient.SoramitsuNetworkClient
import jp.co.soramitsu.xnetworking.sorawallet.core.datasources.blockexplorer.api.models.AssetsInfoResponse
import jp.co.soramitsu.xnetworking.sorawallet.core.datasources.blockexplorer.impl.rest.BasicCases
import jp.co.soramitsu.xnetworking.sorawallet.core.datasources.blockexplorer.impl.rest.assets.case0.SoraWalletAssetsCase0Response
import jp.co.soramitsu.xnetworking.sorawallet.core.datasources.blockexplorer.impl.rest.assets.case0.graphQLRequestSoraWalletAssetsCase0

internal interface SoraWalletAssetsCase {

    suspend fun getAssetsInfo(
        url: String,
        networkClient: SoramitsuNetworkClient,
        tokenIds: List<String>,
        timestamp: String,
    ): List<AssetsInfoResponse>
}

internal object SoraWalletAssetsCases : BasicCases<SoraWalletAssetsCase>() {
    override fun provideInstance(caseName: String): SoraWalletAssetsCase {
        return when (caseName) {
            "0" -> SoraWalletAssetsCase0()
            else -> throw IllegalArgumentException("SoraWalletAssetsCases [$caseName] not found")
        }
    }
}

private class SoraWalletAssetsCase0 : SoraWalletAssetsCase {
    override suspend fun getAssetsInfo(
        url: String,
        networkClient: SoramitsuNetworkClient,
        tokenIds: List<String>,
        timestamp: String,
    ): List<AssetsInfoResponse> {
        var cursor = ""
        val result = mutableListOf<AssetsInfoResponse>()
        val tokenIdsFormatted = tokenIds.joinToString(
            separator = ",",
        ) { "\"$it\"" }
        while (true) {
            val response = networkClient.createJsonRequest<SoraWalletAssetsCase0Response>(
                url,
                HttpMethod.Post,
                SubQueryRequest(graphQLRequestSoraWalletAssetsCase0(cursor, tokenIdsFormatted, timestamp)),
            )
            response.data.entities.nodes.forEach { node ->
                val prevPrice = node.periods.nodes.lastOrNull()?.price?.open?.toDoubleNan()
                result.add(
                    AssetsInfoResponse(
                        node.id,
                        node.liquidity,
                        prevPrice
                    )
                )
            }
            if (response.data.entities.pageInfo.hasNextPage && response.data.entities.pageInfo.endCursor != null) {
                cursor = response.data.entities.pageInfo.endCursor!!
            } else {
                break
            }
        }
        return result
    }
}
