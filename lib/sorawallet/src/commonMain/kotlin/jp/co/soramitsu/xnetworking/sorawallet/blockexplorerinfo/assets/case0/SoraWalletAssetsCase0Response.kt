package jp.co.soramitsu.xnetworking.sorawallet.blockexplorerinfo.assets.case0

import jp.co.soramitsu.xnetworking.basic.common.ResponsePageInfo
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SoraWalletAssetsCase0Response(
    @SerialName("data")
    val data: SoraWalletAssetsCase0ResponseData,
)

@Serializable
data class SoraWalletAssetsCase0ResponseData(
    @SerialName("entities")
    val entities: SoraWalletAssetsCase0ResponseDataEntities,
)

@Serializable
data class SoraWalletAssetsCase0ResponseDataEntities(
    @SerialName("nodes")
    val nodes: List<SoraWalletAssetsCase0ResponseDataEntitiesNode>,
    @SerialName("pageInfo")
    val pageInfo: ResponsePageInfo,
)

@Serializable
data class SoraWalletAssetsCase0ResponseDataEntitiesNode(
    @SerialName("id")
    val id: String,
    @SerialName("liquidity")
    val liquidity: String,
    @SerialName("hourSnapshots")
    val periods: SoraWalletAssetsCase0ResponseDataEntitiesNodeHour,
)

@Serializable
data class SoraWalletAssetsCase0ResponseDataEntitiesNodeHour(
    @SerialName("nodes")
    val nodes: List<SoraWalletAssetsCase0ResponseDataEntitiesNodeHourNode>,
)

@Serializable
data class SoraWalletAssetsCase0ResponseDataEntitiesNodeHourNode(
    @SerialName("priceUSD")
    val price: SoraWalletAssetsCase0ResponseDataEntitiesNodeHourNodePrice,
)

@Serializable
data class SoraWalletAssetsCase0ResponseDataEntitiesNodeHourNodePrice(
    @SerialName("low")
    val low: String,
    @SerialName("high")
    val high: String,
    @SerialName("open")
    val open: String,
    @SerialName("close")
    val close: String,
)
