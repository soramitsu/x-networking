package jp.co.soramitsu.xnetworking.lib.datasources.txhistory.impl.domain.adapters.reef

import jp.co.soramitsu.xnetworking.lib.datasources.txhistory.api.models.TxFilter
import jp.co.soramitsu.xnetworking.lib.engines.utils.GraphQLResponseDataWrapper
import jp.co.soramitsu.xnetworking.lib.engines.utils.GraphQLSerializableRequestWrapper
import jp.co.soramitsu.xnetworking.lib.engines.utils.JsonPostRequest
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Suppress("FunctionName")
internal inline fun ReefRequest(
    url: String,
    address: String,
    limit: Int,
    cursor: String?,
    txFilter: TxFilter
) = JsonPostRequest(
    url = url,
    body = GraphQLSerializableRequestWrapper(
        when(txFilter) {
            TxFilter.TRANSFER -> transfersRequest(address, limit, cursor)
            TxFilter.EXTRINSIC -> extrinsicsRequest(address, limit, cursor)
            TxFilter.REWARD -> rewardsRequest(address, limit, cursor)
        }
    ),
    responseDeserializer = GraphQLResponseDataWrapper.serializer(
        ReefResponse.serializer()
    )
)

internal inline fun transfersRequest(
    address: String,
    limit: Int,
    cursor: String?
) = """
    query MyQuery {
      transfersConnection(
        where: {
          AND: [
            {
              type_eq: Native
            }, 
            {
              OR: [
                {
                  from: {
                    id_eq: "$address"
                  }
                }, 
                {
                  to: {
                    id_eq: "$address"
                  }
                }
              ]
            }
          ]
        }, 
        orderBy: timestamp_DESC, 
        first: $limit, 
        after: ${cursor?.let { "\"$it\"" }}
      ) {
        edges {
          node {
            id
            amount
            timestamp
            success
            to {
              id
            }
            from {
              id
            }
            signedData
            extrinsicHash
          }
        }
        pageInfo {
          endCursor
          hasNextPage
          startCursor
        }
      }
    }
""".trimIndent()

internal inline fun rewardsRequest(
    address: String,
    limit: Int,
    cursor: String?
) = """
    query MyQuery {
    stakingsConnection(
      orderBy: timestamp_DESC, 
      where: {
        AND: {
          signer: {
            id_eq: "$address"
          }, 
          amount_gt: "0"
        }
      }, 
      first: $limit, 
      after: ${cursor?.let { "\"$it\"" }}
    ) {
        edges {
          node {
            id
            amount
            timestamp
            signer {
              id
            }
          }
        }
        pageInfo {
          endCursor
          hasNextPage
        }
      }
    }
""".trimIndent()

internal inline fun extrinsicsRequest(
    address: String,
    limit: Int,
    cursor: String?
) = """
    query MyQuery {
      extrinsicsConnection(
        orderBy: id_ASC, 
        where: {
          signer_eq: "$address"
        }, 
        first: $limit, 
        after: ${cursor?.let { "\"$it\"" }}
      ) {
        edges {
          node {
            id
            hash
            method
            section
            signedData
            status
            signer
            timestamp
            type
          }
        }
        pageInfo {
          endCursor
          hasNextPage
        }
      }
    }
""".trimIndent()


@Serializable
internal class ReefResponse(
    val transfersConnection: TransfersConnection? = null,
    @SerialName("stakingsConnection")
    val rewardsConnection: RewardsConnection? = null,
    val extrinsicsConnection: ExtrinsicsConnection? = null
) {
    @Serializable
    class TransfersConnection(
        val pageInfo: PageInfo,
        val edges: List<Node>
    ) {
        @Serializable
        class Node(
            val node: TransferElement
        ) {
            @Serializable
            class TransferElement(
                val id: String? = null,
                val amount: String,
                val type: String,
                val timestamp: String,
                val success: Boolean,
                val to: ReefAddress,
                val from: ReefAddress,
                val extrinsicHash: String?,
                val signedData: ReefSignedData?
            )
        }
    }

    @Serializable
    class RewardsConnection(
        val pageInfo: PageInfo,
        val edges: List<Node>
    ) {
        @Serializable
        class Node(
            val node: RewardElement
        ) {
            @Serializable
            class RewardElement(
                val id: String,
                val type: String,
                val amount: String,
                val timestamp: String,
                val signer: ReefAddress
            )
        }
    }

    @Serializable
    class ExtrinsicsConnection(
        val pageInfo: PageInfo,
        val edges: List<Node>
    ) {
        @Serializable
        class Node(
            val node: ExtrinsicElement
        ) {
            @Serializable
            class ExtrinsicElement(
                val id: String,
                val hash: String,
                val method: String,
                val section: String,
                val signedData: ReefSignedData?,
                val status: String,
                val signer: String,
                val timestamp: String,
                val type: String
            )
        }
    }

    @Serializable
    class PageInfo(
        val endCursor: String,
        val hasNextPage: Boolean,
        val startCursor: String? = null
    )


    @Serializable
    class ReefAddress(
        val id: String
    )

    @Serializable
    class ReefSignedData(
        val fee: ReefFeeData?
    ) {
        @Serializable
        class ReefFeeData(
            val partialFee: String?
        )
    }
}