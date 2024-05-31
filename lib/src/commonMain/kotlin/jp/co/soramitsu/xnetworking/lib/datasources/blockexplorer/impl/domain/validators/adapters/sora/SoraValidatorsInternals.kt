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
                where: {
                  AND: {
                    era: {
                      index_gte: $eraFrom, 
                      index_lte: $eraTo
                    }
                  }, 
                  staker: {
                    id_eq: "$accountAddress"
                  }
                }
              ) {
                nominations {
                  validator {
                    validator {
                      id
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
    val stakingEraNominators: List<Nominator>
) {
    @Serializable
    class Nominator(
        val nominations: List<Nomination>
    ) {
        @Serializable
        class Nomination(
            val validator: Validator
        ) {
            @Serializable
            class Validator(
                val id: String? = null
            )
        }
    }
}