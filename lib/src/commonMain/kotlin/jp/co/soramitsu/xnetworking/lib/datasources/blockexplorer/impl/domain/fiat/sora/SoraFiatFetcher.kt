package jp.co.soramitsu.xnetworking.lib.datasources.blockexplorer.impl.domain.fiat.sora

import jp.co.soramitsu.xnetworking.lib.datasources.blockexplorer.api.adapters.FiatFetcher
import jp.co.soramitsu.xnetworking.lib.datasources.blockexplorer.api.models.Fiat
import jp.co.soramitsu.xnetworking.lib.datasources.chainsconfig.api.ConfigDAO
import jp.co.soramitsu.xnetworking.lib.engines.rest.api.RestClient
import jp.co.soramitsu.xnetworking.lib.engines.utils.wrapToGraphQLString

class SoraFiatFetcher(
    private val restClient: RestClient,
    private val configDAO: ConfigDAO
): FiatFetcher() {

    override suspend fun fetch(
        chainId: String
    ): List<Fiat> {
        val result = mutableListOf<Fiat>()

        var cursor = ""

        while (true) {
            val response = restClient.post(
                request = SoraFiatRequest(
                    url = configDAO.historyUrl(chainId),
                    pageCount = 100,
                    cursor = cursor.wrapToGraphQLString()
                )
            ).data.entities

            response.nodes.filterNotNull().forEach { node ->
                node.mapToFiatDataResponse()?.let { result.add(it) }
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

    private fun SoraFiatResponse.Entities.Node.mapToFiatDataResponse(): Fiat? {
        return Fiat(
            id = id ?: return null,
            priceUSD = priceUSD ?: return null
        )
    }

}