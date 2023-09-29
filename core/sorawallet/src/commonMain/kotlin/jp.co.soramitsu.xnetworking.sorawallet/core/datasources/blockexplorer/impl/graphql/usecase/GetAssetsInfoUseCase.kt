package jp.co.soramitsu.xnetworking.sorawallet.core.datasources.blockexplorer.impl.graphql.usecase

import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.api.Optional
import jp.co.soramitsu.xnetworking.GetAssetsInfoQuery
import jp.co.soramitsu.xnetworking.basic.common.Utils.toDoubleNan
import jp.co.soramitsu.xnetworking.sorawallet.core.datasources.blockexplorer.api.models.AssetsInfoResponse

internal class GetAssetsInfoUseCase {

    suspend operator fun invoke(
        apolloClient: ApolloClient,
        tokenIds: List<String>,
        timeStamp: Int
    ): List<AssetsInfoResponse> {
        val result = mutableListOf<AssetsInfoResponse>()

        var cursor: Any = ""

        while (true) {
            val response = apolloClient.query(
                GetAssetsInfoQuery(
                    pageCount = 100,
                    cursor = cursor,
                    tokenIds = Optional.present(tokenIds),
                    timestamp = timeStamp
                )
            ).execute().data?.entities ?: return emptyList()

            response.nodes.filterNotNull().forEach { node ->
                result.add(node.mapToAssetsInfoResponse())
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

    private fun GetAssetsInfoQuery.Node.mapToAssetsInfoResponse() =
        AssetsInfoResponse(
            id = id,
            liquidity = liquidity.toString(),
            previousPrice = hourSnapshots.nodes.lastOrNull()?.priceUSD?.toString()
                ?.substringAfter("open=")?.substringBefore(",")?.toDoubleNan() // TODO JSON conversion
        )

}