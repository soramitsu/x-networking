package jp.co.soramitsu.xnetworking.lib.datasources.blockexplorer.impl.domain.apy.adapters.sora

import jp.co.soramitsu.xnetworking.lib.engines.utils.GraphQLResponseDataWrapper
import jp.co.soramitsu.xnetworking.lib.engines.utils.GraphQLSerializableRequestWrapper
import jp.co.soramitsu.xnetworking.lib.engines.utils.JsonPostRequest
import kotlinx.serialization.Serializable


@Suppress("FunctionName")
internal inline fun SoraApyRequest(
    url: String,
    pageCount: Int,
    cursor: String
) = JsonPostRequest(
    url = url,
    body = GraphQLSerializableRequestWrapper(
        """
        query {
            entities:
                poolXYKs(
                    first: $pageCount
                    before: $cursor
                ) {
                    nodes {
                        id
                        strategicBonusApy
                    }
                    pageInfo {
                        endCursor
                    }
                }
        }
        """.trimIndent()
    ),
    responseDeserializer = GraphQLResponseDataWrapper.serializer(
        SoraApyResponse.serializer()
    )
)

@Serializable
internal class SoraApyResponse(
    val entities: Entities
) {
    @Serializable
    class Entities(
        val nodes: List<Node?>,
        val pageInfo: PageInfo
    ) {
        @Serializable
        class Node(
            val id: String?,
            val strategicBonusApy: String?
        )

        @Serializable
        class PageInfo(
            val endCursor: String?
        )
    }
}