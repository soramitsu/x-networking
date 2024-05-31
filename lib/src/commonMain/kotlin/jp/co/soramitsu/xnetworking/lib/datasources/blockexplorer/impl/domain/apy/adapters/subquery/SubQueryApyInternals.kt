package jp.co.soramitsu.xnetworking.lib.datasources.blockexplorer.impl.domain.apy.adapters.subquery

import jp.co.soramitsu.xnetworking.lib.engines.utils.GraphQLResponseDataWrapper
import jp.co.soramitsu.xnetworking.lib.engines.utils.GraphQLSerializableRequestWrapper
import jp.co.soramitsu.xnetworking.lib.engines.utils.JsonPostRequest
import kotlinx.serialization.Serializable

@Suppress("FunctionName")
internal inline fun SubQueryApyRequest(
    url: String,
    collatorIds: List<String>?,
    roundId: Int?
) = JsonPostRequest(
    url = url,
    body = GraphQLSerializableRequestWrapper(
        """
        query {
            collatorRounds(
                filter: {
                    ${
                        collatorIds?.let { ids ->
                            "collatorId: { inInsensitive: ${ids.map { "\"" + it + "\"" }} }"
                        } ?: ""
                    }
                    apr: { 
                        isNull: false, 
                        greaterThan: 0 
                    }
                    roundId: { 
                        equalTo: "$roundId" 
                    }
                }
            ) {
                nodes {
                    collatorId
                    apr
                }
            }
        }
        """.trimIndent()
    ),
    responseDeserializer = GraphQLResponseDataWrapper.serializer(
        SubQueryApyResponse.serializer()
    )
)

@Serializable
internal class SubQueryApyResponse(
    val collatorRounds: CollatorRounds
) {
    @Serializable
    class CollatorRounds(
        val nodes: List<CollatorApyElement>
    ) {
        @Serializable
        class CollatorApyElement(
            val collatorId: String?,
            val apr: String?
        )
    }
}