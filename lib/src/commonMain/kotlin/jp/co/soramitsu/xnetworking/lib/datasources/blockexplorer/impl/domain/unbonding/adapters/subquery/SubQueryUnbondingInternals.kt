package jp.co.soramitsu.xnetworking.lib.datasources.blockexplorer.impl.domain.unbonding.adapters.subquery

import jp.co.soramitsu.xnetworking.lib.engines.utils.GraphQLResponseDataWrapper
import jp.co.soramitsu.xnetworking.lib.engines.utils.GraphQLSerializableRequestWrapper
import jp.co.soramitsu.xnetworking.lib.engines.utils.JsonPostRequest
import kotlinx.serialization.Serializable

@Suppress("FunctionName")
internal inline fun SubQueryUnbondingRequest(
    url: String,
    delegatorAddress: String,
    collatorAddress: String
) = JsonPostRequest(
    url = url,
    body = GraphQLSerializableRequestWrapper(
        """
        query {
          delegatorHistoryElements(
            orderBy: TIMESTAMP_DESC,
            last: 10,
            filter: {
              delegatorId: { 
                equalToInsensitive: "$delegatorAddress"
              },
              collatorId: { 
                equalToInsensitive: "$collatorAddress"
              } 
            }
          ) {
            nodes {
              id
              blockNumber
              delegatorId
              collatorId
              timestamp
              type
              roundId
              amount
            }
          }
        }
        """.trimIndent()
    ),
    responseDeserializer = GraphQLResponseDataWrapper.serializer(
        SubQueryUnbondingResponse.serializer()
    )
)

@Serializable
internal class SubQueryUnbondingResponse(
    val delegatorHistoryElements: DelegatorHistoryElements
) {
    @Serializable
    class DelegatorHistoryElements(
        val nodes: List<HistoryElement>
    ) {
        @Serializable
        class HistoryElement(
            val id: String?,
            val blockNumber: String?,
            val delegatorId: String?,
            val collatorId: String?,
            val timestamp: String?,
            val type: String?,
            val roundId: String?,
            val amount: String?
        )
    }
}