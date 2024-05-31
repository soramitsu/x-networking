package jp.co.soramitsu.xnetworking.lib.datasources.blockexplorer.impl.domain.validators.adapters.subquery

import jp.co.soramitsu.xnetworking.lib.engines.utils.GraphQLResponseDataWrapper
import jp.co.soramitsu.xnetworking.lib.engines.utils.GraphQLSerializableRequestWrapper
import jp.co.soramitsu.xnetworking.lib.engines.utils.JsonPostRequest
import kotlinx.serialization.Serializable

@Suppress("FunctionName")
internal inline fun SubQueryValidatorsRequest(
    url: String,
    accountAddress: String,
    eraFrom: String,
    eraTo: String
) = JsonPostRequest(
    url = url,
    body = GraphQLSerializableRequestWrapper(
        """
            {
                query {
                    eraValidatorInfos(
                        filter: {
                            era: { 
                                greaterThanOrEqualTo: $eraFrom, 
                                lessThanOrEqualTo: $eraTo
                            },
                            others: { 
                                contains:[
                                    {
                                        who: "$accountAddress"
                                    }
                                ]
                            }
                        }
                    ) {
                        nodes {
                            id
                            address
                            era
                            total
                            own
                        }
                    }
                }
            }
        """.trimIndent()
    ),
    responseDeserializer = GraphQLResponseDataWrapper.serializer(
        SubQueryValidatorsResponse.serializer()
    )
)

@Serializable
internal class SubQueryValidatorsResponse(
    val query: EraValidatorInfo?
) {
    @Serializable
    class EraValidatorInfo(
        val eraValidatorInfos: Nodes?
    ) {
        @Serializable
        class Nodes(
            val nodes: List<Node>?
        ) {
            @Serializable
            class Node(
                val id: String,
                val address: String,
                val era: String,
                val total: String,
                val own: String
            )
        }
    }
}