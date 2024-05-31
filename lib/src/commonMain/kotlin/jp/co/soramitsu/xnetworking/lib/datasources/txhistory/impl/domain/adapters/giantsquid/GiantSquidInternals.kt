package jp.co.soramitsu.xnetworking.lib.datasources.txhistory.impl.domain.adapters.giantsquid

import jp.co.soramitsu.xnetworking.lib.engines.utils.GraphQLResponseDataWrapper
import jp.co.soramitsu.xnetworking.lib.engines.utils.GraphQLSerializableRequestWrapper
import jp.co.soramitsu.xnetworking.lib.engines.utils.JsonPostRequest
import kotlinx.serialization.Serializable

@Suppress("FunctionName")
internal inline fun GiantSquidRequest(
    url: String,
    address: String
) = JsonPostRequest(
    url = url,
    body = GraphQLSerializableRequestWrapper(
        """
            query MyQuery {
              transfers(
                where: {
                  account: {
                    id_eq: "$address"
                  }
                }, 
                orderBy: id_DESC
              ) {
                id
                transfer {
                  amount
                  blockNumber
                  extrinsicHash
                  from {
                    id
                  }
                  to {
                    id
                  }
                  timestamp
                  success
                  id
                }
                direction
              }
            }
        """.trimIndent()
    ),
    responseDeserializer = GraphQLResponseDataWrapper.serializer(
        GiantSquidResponse.serializer()
    )
)

@Serializable
internal class GiantSquidResponse(
    val transfers: List<TransferIdWrapper>?,
    val rewards: List<Reward>?,
    val bonds: List<Bond>?,
    val slashes: List<Slash>?
) {
    @Serializable
    class GiantSquidAccount(
        val id: String
    )

    @Serializable
    class TransferIdWrapper(
        val id: String,
        val transfer: Transfer
    ) {
        @Serializable
        class Transfer(
            val amount: String,
            val blockNumber: String,
            val extrinsicHash: String?,
            val from: GiantSquidAccount?,
            val to: GiantSquidAccount?,
            val timestamp: String,
            val success: Boolean,
            val id: String,
        )
    }

    @Serializable
    class Reward(
        val id: String,
        val timestamp: String,
        val blockNumber: Int,
        val extrinsicHash: String?,
        val amount: String,
        val era: String?,
        val validatorId: String?,
        val account: GiantSquidAccount?
    )

    @Serializable
    class Bond(
        val id: String,
        val accountId: String,
        val amount: String,
        val blockNumber: String,
        val extrinsicHash: String?,
        val success: Boolean?,
        val timestamp: String,
        val type: String?
    )

    @Serializable
    class Slash(
        val id: String,
        val accountId: String,
        val amount: String,
        val blockNumber: String,
        val era: String,
        val timestamp: String
    )
}