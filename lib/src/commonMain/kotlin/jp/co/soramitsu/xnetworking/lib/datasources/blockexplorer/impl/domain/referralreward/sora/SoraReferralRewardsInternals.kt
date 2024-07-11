package jp.co.soramitsu.xnetworking.lib.datasources.blockexplorer.impl.domain.referralreward.sora

import jp.co.soramitsu.xnetworking.lib.engines.utils.GraphQLResponseDataWrapper
import jp.co.soramitsu.xnetworking.lib.engines.utils.GraphQLSerializableRequestWrapper
import jp.co.soramitsu.xnetworking.lib.engines.utils.JsonPostRequest
import kotlinx.serialization.Serializable


@Suppress("FunctionName")
internal inline fun SoraReferralRewardsRequest(
    url: String,
    pageCount: Int,
    cursor: String,
    address: String
) = JsonPostRequest(
    url = url,
    body = GraphQLSerializableRequestWrapper(
        """
        query {
            entities:
                referrerRewards(
                    first: $pageCount
                    before: $cursor
                    filter: {
                        referrer: { equalTo: $address }
                    }
                ) {
                    nodes {
                        referral
                        amount
                    }
                    pageInfo {
                        endCursor
                        hasNextPage
                    }
                }
        }
        """.trimIndent()
    ),
    responseDeserializer = GraphQLResponseDataWrapper.serializer(
        SoraReferralRewardsResponse.serializer()
    )
)

@Serializable
internal class SoraReferralRewardsResponse(
    val entities: Entities
) {
    @Serializable
    class Entities(
        val nodes: List<Node?>,
        val pageInfo: PageInfo
    ) {
        @Serializable
        class Node(
            val referral: String?,
            val amount: String?
        )

        @Serializable
        class PageInfo(
            val hasNextPage: Boolean?,
            val endCursor: String?
        )
    }
}