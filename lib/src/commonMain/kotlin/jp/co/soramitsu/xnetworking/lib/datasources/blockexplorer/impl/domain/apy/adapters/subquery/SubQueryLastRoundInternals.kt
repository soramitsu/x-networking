package jp.co.soramitsu.xnetworking.lib.datasources.blockexplorer.impl.domain.apy.adapters.subquery

import jp.co.soramitsu.xnetworking.lib.engines.utils.GraphQLResponseDataWrapper
import jp.co.soramitsu.xnetworking.lib.engines.utils.GraphQLSerializableRequestWrapper
import jp.co.soramitsu.xnetworking.lib.engines.utils.JsonPostRequest
import kotlinx.serialization.Serializable

@Suppress("FunctionName")
internal fun SubQueryLastRoundRequest(
    url: String
) = JsonPostRequest(
    url = url,
    body = GraphQLSerializableRequestWrapper(
        """
            query {
              rounds(last: 1) {
                nodes {
                  id
                }
              }
            }
        """.trimIndent()
    ),
    responseDeserializer = GraphQLResponseDataWrapper.serializer(
        SubQueryLastRoundResponse.serializer()
    )
)

@Serializable
internal class SubQueryLastRoundResponse(
    val rounds: Rounds
) {
    @Serializable
    class Rounds(
        val nodes: List<RoundIdElement>
    ) {
        @Serializable
        class RoundIdElement(
            val id: String?
        )
    }
}