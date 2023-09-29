package jp.co.soramitsu.xnetworking.sorawallet.core.datasources.polkaswapconfig.api.models.mobile

import jp.co.soramitsu.xnetworking.sorawallet.core.datasources.polkaswapconfig.api.models.mobile.CurrencyInfo
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class MobileConfig(
    @SerialName("explorer_type_fiat")
    val explorerTypeFiat: String,
    @SerialName("explorer_type_sbapy")
    val explorerTypeSbapy: String,
    @SerialName("explorer_type_reward")
    val explorerTypeReward: String,
    @SerialName("explorer_type_assets")
    val explorerTypeAssets: String,
    @SerialName("join_link")
    val joinLink: String,
    @SerialName("substrate_types_android")
    val substrateTypesAndroid: String,
    @SerialName("substrate_types_ios")
    val substrateTypesIos: String,
    @SerialName("soracard")
    val soracard: Boolean = false,
    @SerialName("currencies")
    val currencies: List<CurrencyInfo>,
)