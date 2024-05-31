package jp.co.soramitsu.xnetworking.lib.datasources.blockexplorer.impl.domain.unbonding.adapters.subsquid

import jp.co.soramitsu.xnetworking.lib.engines.utils.GraphQLResponseDataWrapper
import jp.co.soramitsu.xnetworking.lib.engines.utils.GraphQLSerializableRequestWrapper
import jp.co.soramitsu.xnetworking.lib.engines.utils.JsonPostRequest
import kotlinx.serialization.Serializable

@Suppress("FunctionName")
internal inline fun SubSquidUnbondingRequest(
    url: String,
    delegatorAddress: String,
    collatorAddress: String
) = JsonPostRequest(
    url = url,
    body = GraphQLSerializableRequestWrapper(
        """
           query MyQuery {
            historyElements(
              where: {
                staker: {
                  id_eq: "$delegatorAddress"
                }, 
                amount_isNull: false, 
                collator: {
                  id_eq: "$collatorAddress"
                }
              }
            ) {
              id
              amount
              blockNumber
              timestamp
              type
            }
          }
        """.trimIndent()
    ),
    responseDeserializer = GraphQLResponseDataWrapper.serializer(
        SubSquidUnbondingResponse.serializer()
    )
)

@Serializable
internal class SubSquidUnbondingResponse(
    val rewards: List<Reward>
) {
    @Serializable
    class Reward(
        val id: String,
        val amount: String?,
        val blockNumber: Int?,
        val round: Int?,
        val timestamp: String?,
        val extrinsicHash: String?
    )
}