package jp.co.soramitsu.xnetworking.lib.datasources.blockexplorer.impl.domain.apy.adapters.subquid

import jp.co.soramitsu.xnetworking.lib.engines.utils.GraphQLResponseDataWrapper
import jp.co.soramitsu.xnetworking.lib.engines.utils.GraphQLSerializableRequestWrapper
import jp.co.soramitsu.xnetworking.lib.engines.utils.JsonPostRequest
import kotlinx.serialization.Serializable

@Suppress("FunctionName")
internal inline fun SubSquidApyRequest(
    url: String
) = JsonPostRequest(
    url = url,
    body = GraphQLSerializableRequestWrapper(
        """
        query MyQuery {
          stakers(
            where: {
              role_eq: "collator"
            }
          ) {
            stashId
            apr24h
          }
        }
        """.trimIndent()
    ),
    responseDeserializer = GraphQLResponseDataWrapper.serializer(
        SubSquidApyResponse.serializer()
    )
)

@Serializable
internal class SubSquidApyResponse(
    val stakers: List<CollatorApyElement>
) {
    @Serializable
    class CollatorApyElement(
        val stashId: String?,
        val apr24h: String?
    )
}