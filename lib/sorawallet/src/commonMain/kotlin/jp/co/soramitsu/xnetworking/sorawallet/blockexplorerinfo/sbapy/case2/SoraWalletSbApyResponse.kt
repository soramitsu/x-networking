package jp.co.soramitsu.xnetworking.sorawallet.blockexplorerinfo.sbapy.case2

import jp.co.soramitsu.xnetworking.basic.common.ResponsePageInfo
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SoraWalletSbApyCase2Response(
    @SerialName("data")
    val data: SoraWalletSbApyCase2ResponseData,
)

@Serializable
data class SoraWalletSbApyCase2ResponseData(
    @SerialName("entities")
    val entities: SoraWalletSbApyCase2ResponseDataEntities,
)

@Serializable
data class SoraWalletSbApyCase2ResponseDataEntities(
    @SerialName("nodes")
    val nodes: List<SoraWalletSbApyCase2ResponseDataEntitiesNode>,
    @SerialName("pageInfo")
    val pageInfo: ResponsePageInfo,
)

@Serializable
data class SoraWalletSbApyCase2ResponseDataEntitiesNode(
    @SerialName("id")
    val id: String,
    @SerialName("strategicBonusApy")
    val strategicBonusApy: String? = null,
)
