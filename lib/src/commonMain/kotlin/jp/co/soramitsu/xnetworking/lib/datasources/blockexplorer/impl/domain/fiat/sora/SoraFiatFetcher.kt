package jp.co.soramitsu.xnetworking.lib.datasources.blockexplorer.impl.domain.fiat.sora

import jp.co.soramitsu.xnetworking.lib.datasources.blockexplorer.api.adapters.FiatFetcher
import jp.co.soramitsu.xnetworking.sorawallet.GetFiatDataQuery
import jp.co.soramitsu.xnetworking.lib.datasources.blockexplorer.api.models.Fiat
import jp.co.soramitsu.xnetworking.lib.datasources.chainsconfig.api.ConfigDAO
import jp.co.soramitsu.xnetworking.lib.engines.apollo.api.ApolloClientStore

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
                configDAO.stakingUrl(chainId),
                GetFiatDataQuery(
                    pageCount = 100,
                    cursor = cursor
                )
            ).entities ?: return emptyList()

            response.nodes.filterNotNull().forEach { node ->
                result.add(node.mapToFiatDataResponse())
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

    private fun GetFiatDataQuery.Node.mapToFiatDataResponse() =
        Fiat(
            id = id,
            priceUSD = priceUSD
        )

}