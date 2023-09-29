package jp.co.soramitsu.xnetworking.sorawallet.core.datasources.blockexplorer.impl.graphql.usecase

import com.apollographql.apollo3.ApolloClient
import jp.co.soramitsu.xnetworking.GetReferrerRewardsQuery
import jp.co.soramitsu.xnetworking.sorawallet.core.datasources.blockexplorer.api.models.ReferrerRewardResponse

internal class GetReferrerRewardsUseCase {

    suspend operator fun invoke(
        apolloClient: ApolloClient,
        address: String
    ): List<ReferrerRewardResponse> {
        val result = mutableListOf<ReferrerRewardResponse>()

        var cursor: Any = ""

        while (true) {
            val response = apolloClient.query(
                GetReferrerRewardsQuery(
                    pageCount = 100,
                    cursor = cursor,
                    address = address
                )
            ).execute().data?.entities ?: return emptyList()

            response.nodes.filterNotNull().forEach { node ->
                result.add(node.mapReferrerRewardsResponse())
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

    private fun GetReferrerRewardsQuery.Node.mapReferrerRewardsResponse() =
        ReferrerRewardResponse(
            referral = referral,
            amount = amount.toString()
        )

}