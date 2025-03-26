package jp.co.soramitsu.xnetworking.lib.datasources.blockexplorer.impl.domain.apy.adapters.sora

import jp.co.soramitsu.xnetworking.lib.datasources.blockexplorer.api.adapters.ApyFetcher
import jp.co.soramitsu.xnetworking.lib.datasources.blockexplorer.api.models.Apy
import jp.co.soramitsu.xnetworking.lib.datasources.chainsconfig.api.ConfigDAO
import jp.co.soramitsu.xnetworking.lib.engines.apollo.api.ApolloClientStore
import jp.co.soramitsu.xnetworking.mainnet.GetSbApyInfoQuery

class SoraApyFetcher(
    private val apolloClientStore: ApolloClientStore,
    private val configDAO: ConfigDAO
) : ApyFetcher() {

    override suspend fun fetch(
        chainId: String,
        selectedCandidates: List<String>?
    ): List<Apy> {
        val result = mutableListOf<Apy>()
        var cursor = ""

        while (true) {
            val response = apolloClientStore.query(
                configDAO.historyUrl(chainId),
                GetSbApyInfoQuery(
                    cursor = cursor
                )
            ).data ?: return emptyList()

            response.edges.forEach { edge ->
                if (edge.node?.id == null || edge.node.strategicBonusApy == null)
                    return@forEach
                result.add(
                    Apy(
                        id = edge.node.id,
                        value = edge.node.strategicBonusApy,
                    )
                )
            }

            if (response.pageInfo.hasNextPage.not()) break
            val endCursor = response.pageInfo.endCursor ?: break
            cursor = endCursor
        }

        return result
    }

}