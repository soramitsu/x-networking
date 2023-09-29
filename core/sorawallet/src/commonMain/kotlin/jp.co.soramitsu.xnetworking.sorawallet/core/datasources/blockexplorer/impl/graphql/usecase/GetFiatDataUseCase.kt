package jp.co.soramitsu.xnetworking.sorawallet.core.datasources.blockexplorer.impl.graphql.usecase

import com.apollographql.apollo3.ApolloClient
import jp.co.soramitsu.xnetworking.GetFiatDataQuery
import jp.co.soramitsu.xnetworking.sorawallet.core.datasources.blockexplorer.api.models.FiatDataResponse

internal class GetFiatDataUseCase {

    suspend operator fun invoke(
        apolloClient: ApolloClient
    ): List<FiatDataResponse> {
        val result = mutableListOf<FiatDataResponse>()

        var cursor: Any = ""

        while (true) {
            val response = apolloClient.query(
                GetFiatDataQuery(
                    pageCount = 100,
                    cursor = cursor
                )
            ).execute().data?.entities ?: return emptyList()

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
        FiatDataResponse(
            id = id,
            priceUSD = priceUSD
        )

}