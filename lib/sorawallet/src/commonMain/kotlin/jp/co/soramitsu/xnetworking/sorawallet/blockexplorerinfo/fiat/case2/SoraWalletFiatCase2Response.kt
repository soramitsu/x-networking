package jp.co.soramitsu.xnetworking.sorawallet.blockexplorerinfo.fiat.case2

import jp.co.soramitsu.xnetworking.basic.common.ResponsePageInfo
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SoraWalletFiatCase2Response(
    @SerialName("data")
    val data: SoraWalletFiatCase2ResponseData,
)

@Serializable
data class SoraWalletFiatCase2ResponseData(
    @SerialName("entities")
    val entities: SoraWalletFiatCase2ResponseDataEntities,
)

@Serializable
data class SoraWalletFiatCase2ResponseDataEntities(
    @SerialName("nodes")
    val nodes: List<SoraWalletFiatCase2ResponseDataEntitiesNode>,
    @SerialName("pageInfo")
    val pageInfo: ResponsePageInfo,
)

@Serializable
data class SoraWalletFiatCase2ResponseDataEntitiesNode(
    @SerialName("id")
    val id: String,
    @SerialName("priceUSD")
    val priceUSD: String,
)
