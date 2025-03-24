package jp.co.soramitsu.xnetworking.lib.datasources.blockexplorer.impl.domain.fiat.sora

import jp.co.soramitsu.xnetworking.lib.datasources.blockexplorer.api.adapters.FiatFetcher
import jp.co.soramitsu.xnetworking.lib.datasources.blockexplorer.api.models.Fiat
import jp.co.soramitsu.xnetworking.lib.datasources.chainsconfig.api.ConfigDAO
import jp.co.soramitsu.xnetworking.lib.engines.apollo.api.ApolloClientStore
import jp.co.soramitsu.xnetworking.mainnet.GetFiatDataQuery

class SoraFiatFetcher(
    private val apolloClientStore: ApolloClientStore,
    private val configDAO: ConfigDAO
): FiatFetcher() {

    override suspend fun fetch(
        chainId: String
    ): List<Fiat> {
        val result = mutableListOf<Fiat>()

        var cursor = ""

        while (true) {
            val response = apolloClientStore.query(
                configDAO.historyUrl(chainId),
                GetFiatDataQuery(
                    pageCount = 100,
                    cursor = cursor
                )
            ).entities ?: return emptyList()

            response.nodes.filterNotNull().forEach { node ->
                node.mapToFiatDataResponse()?.let { result.add(it) }
            }

            val (hasNextPage, endCursor) = response.pageInfo.run {
                hasNextPage to endCursor
            }

            if (!hasNextPage || endCursor == null)
                break

            cursor = endCursor
        }

        return result
    }

    private fun GetFiatDataQuery.Node.mapToFiatDataResponse(): Fiat? {
        return Fiat(
            id = id ?: return null,
            priceUSD = priceUSD ?: return null
        )
    }

}