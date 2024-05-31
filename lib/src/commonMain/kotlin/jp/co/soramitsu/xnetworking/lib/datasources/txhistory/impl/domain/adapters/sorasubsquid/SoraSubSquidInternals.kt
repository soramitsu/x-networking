package jp.co.soramitsu.xnetworking.lib.datasources.txhistory.impl.domain.adapters.sorasubsquid

import jp.co.soramitsu.xnetworking.lib.engines.utils.GraphQLResponseDataWrapper
import jp.co.soramitsu.xnetworking.lib.engines.utils.GraphQLSerializableRequestWrapper
import jp.co.soramitsu.xnetworking.lib.engines.utils.JsonPostRequest
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject

@Suppress("FunctionName")
internal inline fun SoraSubSquidRequest(
    url: String,
    address: String,
    limit: Int,
    cursor: String?
) = JsonPostRequest(
    url = url,
    body = GraphQLSerializableRequestWrapper(
        query = """
            query MyQuery(
              $limit: Int, 
              $address: String, 
              $cursor: String
            ) {
              historyElementsConnection(
                where: {
                  address_eq: $address, 
                  OR: {
                    dataTo_eq: $address
                  }
                }, 
                first: $limit, 
                after: $cursor, 
                orderBy: timestamp_DESC
              ) {
                pageInfo {
                  endCursor
                  hasNextPage
                }
                edges {
                  cursor
                  node {
                    address
                    blockHash
                    blockHeight
                    data
                    id
                    method
                    module
                    name
                    networkFee
                    timestamp
                    execution {
                      error {
                        moduleErrorId
                        moduleErrorIndex
                        nonModuleErrorMessage
                      }
                      success
                    }
                  }
                }
              }
            }
        """.trimIndent()
    ),
    responseDeserializer = GraphQLResponseDataWrapper.serializer(
        SoraSubSquidResponse.serializer()
    )
)

@Serializable
class SoraSubSquidResponse(
    val historyElementsConnection: HistoryElementsConnection
) {
    @Serializable
    class HistoryElementsConnection(
        val edges: List<Edge>,
        val pageInfo: PageInfo,
    ) {
        @Serializable
        class Edge(
            val node: Node,
        ) {
            @Serializable
            class Node(
                val id: String = "",
                val timestamp: String = "",
                val networkFee: String? = null,
                val module: String = "",
                val method: String = "",
                val execution: ExecutionResult? = null,
                val address: String = "",
                val blockHash: String = "",
                val data: JsonElement = JsonObject(emptyMap()),
            ) {
                @Serializable
                class ExecutionResult(
                    val success: Boolean,
                    val error: Error? = null,
                ) {
                    @Serializable
                    class Error(
                        val moduleErrorId: String? = null,
                        val moduleErrorIndex: Int? = null,
                        val nonModuleErrorMessage: String? = null
                    )
                }
            }
        }

        @Serializable
        class PageInfo(
            val hasNextPage: Boolean,
            val endCursor: String?,
        )
    }
}
