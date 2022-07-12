package jp.co.soramitsu.commonnetworking.subquery.response

import kotlinx.serialization.Serializable

@Serializable
data class SubQuerySbApyResponse(val data: PoolsInfoResponsePoolXykEntity)

@Serializable
data class PoolsInfoResponsePoolXykEntity(val poolXYKEntities: PoolsInfoResponseNodes)

@Serializable
data class PoolsInfoResponseNodes(val nodes: List<PoolsInfoResponseNodesElement>)

@Serializable
data class PoolsInfoResponseNodesElement(val pools: PoolsInfoResponsePools)

@Serializable
data class PoolsInfoResponsePools(val edges: List<PoolsInfoResponseDataElement>)

@Serializable
data class PoolsInfoResponseDataElement(val node: PoolsInfoResponseData)

@Serializable
data class PoolsInfoResponseData(
    val targetAssetId: String,
    val priceUSD: String? = null,
    val strategicBonusApy: String? = null
)