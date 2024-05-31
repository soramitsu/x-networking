package jp.co.soramitsu.xnetworking.lib.datasources.blockexplorer.impl.domain.apy.adapters.sora

import jp.co.soramitsu.xnetworking.lib.datasources.chainsconfig.api.ConfigDAO
import jp.co.soramitsu.xnetworking.lib.datasources.blockexplorer.api.adapters.ApyFetcher
import jp.co.soramitsu.xnetworking.lib.datasources.blockexplorer.api.models.Apy
import jp.co.soramitsu.xnetworking.lib.engines.apollo.api.ApolloClientStore
import jp.co.soramitsu.xnetworking.sorawallet.GetSbApyInfoQuery

class SoraApyFetcher(
    private val apolloClientStore: ApolloClientStore,
    private val configDAO: ConfigDAO
): ApyFetcher() {

    override suspend fun fetch(
        chainId: String,
        selectedCandidates: List<String>?
    ): List<Apy> {
        val result = mutableListOf<Apy>()

        var cursor = ""

        while (true) {
            val response = apolloClientStore.query(
                configDAO.stakingUrl(chainId),
                GetSbApyInfoQuery(
                    pageCount = 100,
                    cursor = cursor
                )
            ).entities ?: return emptyList()

            response.nodes.filterNotNull().forEach { node ->
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