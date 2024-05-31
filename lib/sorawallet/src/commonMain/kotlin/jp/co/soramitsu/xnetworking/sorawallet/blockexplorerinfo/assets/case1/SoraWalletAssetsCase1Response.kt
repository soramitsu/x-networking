package jp.co.soramitsu.xnetworking.sorawallet.blockexplorerinfo.assets.case1

import jp.co.soramitsu.xnetworking.basic.common.ResponsePageInfo
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SoraWalletAssetsCase1Response(
    @SerialName("data")
    val data: SoraWalletAssetsCase1ResponseData,
)

@Serializable
data class SoraWalletAssetsCase1ResponseData(
    @SerialName("data")
    val entities: SoraWalletAssetsCase1ResponseDataEntities,
)

@Serializable
data class SoraWalletAssetsCase1ResponseDataEntities(
    @SerialName("edges")
    val nodes: List<SoraWalletAssetsCase1ResponseDataEntitiesNode>,
    @SerialName("pageInfo")
    val pageInfo: ResponsePageInfo,
)

@Serializable
data class SoraWalletAssetsCase1ResponseDataEntitiesNode(
    @SerialName("node")
    val node: SoraWalletAssetsCase1ResponseDataEntitiesNodeHour,
)

@Serializable
data class SoraWalletAssetsCase1ResponseDataEntitiesNodeHour(
    @SerialName("id")
    val id: String,
    @SerialName("priceChangeDay")
    val priceChangeDay: Double?,
    @SerialName("liquidity")
    val liquidity: String?,
)
