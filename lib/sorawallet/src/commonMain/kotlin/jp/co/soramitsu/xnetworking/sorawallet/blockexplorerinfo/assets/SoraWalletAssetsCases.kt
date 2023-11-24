package jp.co.soramitsu.xnetworking.sorawallet.blockexplorerinfo.assets

import io.ktor.http.HttpMethod
import jp.co.soramitsu.xnetworking.basic.common.Utils.toDoubleNan
import jp.co.soramitsu.xnetworking.basic.networkclient.SoramitsuNetworkClient
import jp.co.soramitsu.xnetworking.basic.common.BlockExplorerGraphQlRequest
import jp.co.soramitsu.xnetworking.sorawallet.blockexplorerinfo.BasicCases
import jp.co.soramitsu.xnetworking.sorawallet.blockexplorerinfo.assets.case0.SoraWalletAssetsCase0Response
import jp.co.soramitsu.xnetworking.sorawallet.blockexplorerinfo.assets.case0.graphQLRequestSoraWalletAssetsCase0

internal interface SoraWalletAssetsCase {

    suspend fun getAssetsInfo(
        url: String,
        networkClient: SoramitsuNetworkClient,
        tokenIds: List<String>,
        timestamp: Long,
    ): List<AssetsInfo>
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
        timestamp: Long,
    ): List<AssetsInfo> {
        val result = mutableListOf<AssetsInfo>()
        tokenIds.chunked(70).forEach { chunk ->
            var cursor = ""
            val tokenIdsFormatted = chunk.joinToString(
                separator = ",",
            ) { "\"$it\"" }
            while (true) {
                val response = networkClient.createJsonRequest<SoraWalletAssetsCase0Response>(
                    url,
                    HttpMethod.Post,
                    BlockExplorerGraphQlRequest(graphQLRequestSoraWalletAssetsCase0(cursor, tokenIdsFormatted, timestamp.toString())),
                )
                response.data.entities.nodes.forEach { node ->
                    val prevPrice = node.periods.nodes.lastOrNull()?.price?.open?.toDoubleNan()
                    result.add(AssetsInfo(node.id, node.liquidity, prevPrice))
                }
                if (response.data.entities.pageInfo.hasNextPage && response.data.entities.pageInfo.endCursor != null) {
                    cursor = response.data.entities.pageInfo.endCursor!!
                } else {
                    break
                }
            }
        }
        return result
    }
}
