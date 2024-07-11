package jp.co.soramitsu.xnetworking.lib.datasources.blockexplorer.impl.domain.apy.adapters.sora

import jp.co.soramitsu.xnetworking.lib.datasources.chainsconfig.api.ConfigDAO
import jp.co.soramitsu.xnetworking.lib.datasources.blockexplorer.api.adapters.ApyFetcher
import jp.co.soramitsu.xnetworking.lib.datasources.blockexplorer.api.models.Apy
import jp.co.soramitsu.xnetworking.lib.engines.rest.api.RestClient
import jp.co.soramitsu.xnetworking.lib.engines.utils.wrapToGraphQLString

class SoraApyFetcher(
    private val restClient: RestClient,
    private val configDAO: ConfigDAO
): ApyFetcher() {

    override suspend fun fetch(
        chainId: String,
        selectedCandidates: List<String>?
    ): List<Apy> {
        val result = mutableListOf<Apy>()

        var cursor = ""

        while (true) {
            val response = restClient.post(
                request = SoraApyRequest(
                    url = configDAO.historyUrl(chainId),
                    pageCount = 100,
                    cursor = cursor.wrapToGraphQLString()
                )
            ).data.entities

            response.nodes.filterNotNull().forEach { node ->
                if (node.id == null || node.strategicBonusApy == null)
                    return@forEach

                Apy(
                    id = node.id,
                    value = node.strategicBonusApy
                ).apply { result += this }
            }

            val endCursor = response.pageInfo.endCursor ?: break

            cursor = endCursor
        }

        return result
    }

}