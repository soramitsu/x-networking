package jp.co.soramitsu.xnetworking.lib.datasources.txhistory.impl.domain.adapters.subsquid

import jp.co.soramitsu.xnetworking.lib.engines.utils.GraphQLResponseDataWrapper
import jp.co.soramitsu.xnetworking.lib.engines.utils.GraphQLSerializableRequestWrapper
import jp.co.soramitsu.xnetworking.lib.engines.utils.JsonPostRequest
import kotlinx.serialization.Serializable

@Suppress("FunctionName")
internal inline fun SubSquidRequest(
    url: String,
    address: String,
    cursor: String? = null,
    limit: Int
) = JsonPostRequest(
    url = url,
    body = GraphQLSerializableRequestWrapper(
        """
            query MyQuery {
              historyElementsConnection(
                where: {
                  address_eq: "$address"
                }, 
                orderBy: timestamp_DESC, 
                first: $limit, 
                after: "$cursor"
              ) {
                pageInfo {
                  hasNextPage
                  endCursor
                }
                edges {
                  node {
                    timestamp
                    id
                    extrinsicIdx
                    extrinsicHash
                    address
                    success
                    transfer {
                      amount
                      fee
                      from
                      to
                    }
                    reward {
                      amount
                      era
                      stash
                    }
                  }
                }
              }
            }
        """.trimIndent()
    ),
    responseDeserializer = GraphQLResponseDataWrapper.serializer(
        SubSquidResponse.serializer()
    )
)

@Serializable
internal class SubSquidResponse(
    val historyElementsConnection: HistoryElementsConnection
) {
    @Serializable
    class HistoryElementsConnection(
        val pageInfo: PageInfo,
        val edges: List<Node>
    ) {
        @Serializable
        class PageInfo(
            val hasNextPage: Boolean?,
            val endCursor: String
        )

        @Serializable
        class Node(
            val node: HistoryElement
        ) {
            @Serializable
            class HistoryElement(
                val timestamp: Long,
                val id: String,
                val extrinsicIdx: String?,
                val extrinsicHash: String?,
                val address: String,
                val success: Boolean,
                val transfer: Transfer?,
                val reward: Reward?,
            ) {
                @Serializable
                class Reward(
                    val amount: String,
                    val era: Int?,
                    val stash: String?
                )

                @Serializable
                class Transfer(
                    val amount: String,
                    val fee: String?,
                    val from: String,
                    val to: String,
                )
            }
        }
    }
}