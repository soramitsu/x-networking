package jp.co.soramitsu.xnetworking.lib.datasources.blockexplorer.impl.domain.assetinfo.sora

import jp.co.soramitsu.xnetworking.lib.engines.utils.GraphQLResponseDataWrapper
import jp.co.soramitsu.xnetworking.lib.engines.utils.GraphQLSerializableRequestWrapper
import jp.co.soramitsu.xnetworking.lib.engines.utils.JsonPostRequest
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject


@Suppress("FunctionName")
internal inline fun SoraAssetInfoRequest(
    url: String,
    pageCount: Int,
    cursor: String,
    tokenIds: List<String>,
    timestamp: Int
) = JsonPostRequest(
    url = url,
    body = GraphQLSerializableRequestWrapper(
        """
        query {
            entities:
                assets(
                    first: $pageCount
                    after: $cursor
                    filter: {
                        and: [
                            {
                                id: {
                                    in: $tokenIds
                                }
                            }
                        ]
                    }
                ) {
                    nodes {
                        id
                        liquidity
                        hourSnapshots:
                            data(
                                orderBy: TIMESTAMP_DESC
                                filter: {
                                    and: [
                                        {
                                            timestamp: {
                                                greaterThanOrEqualTo: $timestamp
                                            }
                                        }
                                        {
                                            type: {
                                                equalTo: HOUR
                                            }
                                        }
                                    ]
                                }
                            ) {
                                nodes {
                                    priceUSD
                                }
                            }
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
        SoraAssetInfoResponse.serializer()
    )
)

@Serializable
internal class SoraAssetInfoResponse(
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
            val liquidity: String?,
            val hourSnapshots: HourSnapshot?
        ) {
            @Serializable
            class HourSnapshot(
                val nodes: List<Node?>
            ) {
                @Serializable
                class Node(
                    val priceUSD: JsonObject?
                )
            }
        }

        @Serializable
        class PageInfo(
            val hasNextPage: Boolean?,
            val endCursor: String?
        )
    }
}