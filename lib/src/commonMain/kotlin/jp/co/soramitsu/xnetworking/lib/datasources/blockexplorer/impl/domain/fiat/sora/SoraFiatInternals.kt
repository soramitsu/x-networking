package jp.co.soramitsu.xnetworking.lib.datasources.blockexplorer.impl.domain.fiat.sora

import jp.co.soramitsu.xnetworking.lib.engines.utils.GraphQLResponseDataWrapper
import jp.co.soramitsu.xnetworking.lib.engines.utils.GraphQLSerializableRequestWrapper
import jp.co.soramitsu.xnetworking.lib.engines.utils.JsonPostRequest
import kotlinx.serialization.Serializable


@Suppress("FunctionName")
internal inline fun SoraFiatRequest(
    url: String,
    pageCount: Int,
    cursor: String
) = JsonPostRequest(
    url = url,
    body = GraphQLSerializableRequestWrapper(
        """
        query GetFiatData(
            $pageCount: Int!,
            $cursor: Cursor!
        ) {
            entities:
                assets(
                    first: $pageCount
                    after: $cursor
                ) {
                    nodes {
                        id
                        priceUSD
                    }
                    pageInfo {
                        hasNextPage
                        endCursor
                    }
                }
        }
        """.trimIndent()
    ),
    responseDeserializer = GraphQLResponseDataWrapper.serializer(
        SoraFiatResponse.serializer()
    )
)

@Serializable
internal class SoraFiatResponse(
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
            val priceUSD: String?
        )

        @Serializable
        class PageInfo(
            val hasNextPage: Boolean?,
            val endCursor: String?
        )
    }
}