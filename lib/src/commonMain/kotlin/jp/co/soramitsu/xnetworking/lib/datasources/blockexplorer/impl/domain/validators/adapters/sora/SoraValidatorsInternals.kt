package jp.co.soramitsu.xnetworking.lib.datasources.blockexplorer.impl.domain.validators.adapters.sora

import jp.co.soramitsu.xnetworking.lib.engines.utils.GraphQLResponseDataWrapper
import jp.co.soramitsu.xnetworking.lib.engines.utils.GraphQLSerializableRequestWrapper
import jp.co.soramitsu.xnetworking.lib.engines.utils.JsonPostRequest
import kotlinx.serialization.Serializable

@Suppress("FunctionName")
internal inline fun SoraValidatorsRequest(
    url: String,
    accountAddress: String,
    eraFrom: String,
    eraTo: String
) = JsonPostRequest(
    url = url,
    body = GraphQLSerializableRequestWrapper(
        """
            query MyQuery {
              stakingEraNominators(
                filter: {
                  and: {
                    era: {
                      index: {
                        greaterThanOrEqualTo: $eraFrom,
                        lessThanOrEqualTo: $eraTo
                      }
                    },
                    staker: {
                      id: {equalTo: "$accountAddress"}
                    }
                  }
                }
              ) {
                nodes {
                  nominations {
                    nodes {
                      validator {
                        stakerId
                      }
                    }
                  }
                }
              }
            }
        """.trimIndent()
    ),
    responseDeserializer = GraphQLResponseDataWrapper.serializer(
        SoraValidatorsResponse.serializer()
    )
)

@Serializable
internal class SoraValidatorsResponse(
    val stakingEraNominators: SoraValidatorsResponseNodes,
) {
    @Serializable
    class SoraValidatorsResponseNodes(
        val nodes: List<SoraValidatorsResponseNominations>,
    ) {
        @Serializable
        class SoraValidatorsResponseNominations(
            val nominations: SoraValidatorsResponseNominations,
        ) {
            @Serializable
            class SoraValidatorsResponseNominations(
                val nodes: List<SoraValidatorsResponseValidator>,
            ) {
                @Serializable
                class SoraValidatorsResponseValidator(
                    val validator: SoraValidatorsResponse,
                ) {
                    @Serializable
                    class SoraValidatorsResponse(
                        val stakerId: String,
                    )
                }
            }
        }
    }
}