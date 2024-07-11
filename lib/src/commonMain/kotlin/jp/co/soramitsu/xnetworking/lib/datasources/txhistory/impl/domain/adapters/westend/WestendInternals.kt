package jp.co.soramitsu.xnetworking.lib.datasources.txhistory.impl.domain.adapters.westend

import jp.co.soramitsu.xnetworking.lib.engines.utils.GraphQLResponseDataWrapper
import jp.co.soramitsu.xnetworking.lib.engines.utils.GraphQLSerializableRequestWrapper
import jp.co.soramitsu.xnetworking.lib.engines.utils.JsonPostRequest
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject

@Suppress("FunctionName")
internal inline fun WestendRequest(
    url: String,
    pageCount: Int,
    cursor: String? = null,
    address: String,
) = JsonPostRequest(
    url = url,
    body = GraphQLSerializableRequestWrapper(
        """
        query {
            historyElements(
                after: $cursor
                first: $pageCount
                orderBy: TIMESTAMP_DESC
                filter: {
                    address: { equalTo: $address }
                }
            ) {
                pageInfo {
                    endCursor
                    hasNextPage
                }
                nodes {
                    id
                    timestamp
                    address
                    reward
                    transfer
                    extrinsic
                }
            }
        }
        """.trimIndent()
    ),
    responseDeserializer = GraphQLResponseDataWrapper.serializer(
        WestnedResponse.serializer()
    )
)

@Serializable
internal class WestnedResponse(
    val historyElements: HistoryElements
) {
    @Serializable
    class HistoryElements(
        val pageInfo: PageInfo,
        val nodes: List<Node>
    ) {
        @Serializable
        class PageInfo(
            val hasNextPage: Boolean?,
            val endCursor: String?
        )

        @Serializable
        class Node(
            val id: String?,
            val timestamp: Long?,
            val address: String?,
            val reward: JsonObject?,
            val transfer: JsonObject?,
            val extrinsic: JsonObject?
        )
    }
}