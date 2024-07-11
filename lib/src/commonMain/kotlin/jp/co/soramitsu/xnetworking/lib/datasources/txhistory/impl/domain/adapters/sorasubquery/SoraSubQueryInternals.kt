package jp.co.soramitsu.xnetworking.lib.datasources.txhistory.impl.domain.adapters.sorasubquery

import jp.co.soramitsu.xnetworking.lib.engines.utils.GraphQLResponseDataWrapper
import jp.co.soramitsu.xnetworking.lib.engines.utils.GraphQLSerializableRequestWrapper
import jp.co.soramitsu.xnetworking.lib.engines.utils.JsonPostRequest
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject

@Suppress("FunctionName")
internal inline fun SoraSubQueryRequest(
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
                first: $pageCount
                after: $cursor
                orderBy: TIMESTAMP_DESC
                filter: {
                  and: [
                    {
                      or: [ 
                        {
                          or: [
                            { module: { equalTo: "liquidityProxy" } method: { equalTo: "swapTransfer" }} 
                            { module: { equalTo: "liquidityProxy" } method: { equalTo: "swapTransferBatch" }}
                          ]
                        }
                        { 
                          or: [
                            { module: { equalTo: "assets" } method: { equalToInsensitive: "transfer" }} 
                            { module: { equalTo: "liquidityProxy" } method: { equalTo: "xorlessTransfer" }} 
                            { module: { equalTo: "liquidityProxy" } method: { equalTo: "swap" }} 
                            { module: { equalTo: "poolXYK" } method: { equalTo: "depositLiquidity" }} 
                            { data: { contains: [{ method: "depositLiquidity" }] }} 
                            { module: { equalTo: "poolXYK" } method: { equalTo: "withdrawLiquidity" }} 
                            { data: { contains: [{ method: "withdrawLiquidity" }] }} 
                            { module: { equalTo: "referrals" } }
                            { module: { equalTo: "ethBridge" } method: { equalTo: "transferToSidechain" }} 
                            { module: { equalTo: "demeterFarmingPlatform" } method: { equalTo: "deposit" }} 
                            { module: { equalTo: "demeterFarmingPlatform" } method: { equalTo: "withdraw" }} 
                            { module: { equalTo: "demeterFarmingPlatform" } method: { equalTo: "getRewards" }} 
                            { module: { equalTo: "orderBook" } method: { equalTo: "placeLimitOrder" }} 
                          ] 
                        } 
                        { 
                          data: { contains: { to: $address } }
                          module: { equalTo: "assets" } method: { equalTo: "transfer" }
                          execution: { contains: { success: true } }
                        } 
                        {
                          data: { contains: { to: $address } }
                          module: { equalTo: "referrals" } method: { equalTo: "setReferrer" }
                          execution: { contains: { success: true } }
                        }
                      ]
                    }
                    {
                      or: [
                        { address: { equalTo: $address } }
                        { dataTo: { equalTo: $address } }
                      ]
                    }
                  ]  
                } 
            ) {
                nodes {
                    id
                    blockHash
                    module
                    method
                    address
                    networkFee
                    execution
                    timestamp
                    data
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
        SoraSubQueryResponse.serializer()
    )
)

@Serializable
internal class SoraSubQueryResponse(
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
            val blockHash: String?,
            val module: String?,
            val method: String?,
            val address: String?,
            val networkFee: String?,
            val execution: JsonObject?,
            val timestamp: Long?,
            val data: JsonElement?
        )
    }
}