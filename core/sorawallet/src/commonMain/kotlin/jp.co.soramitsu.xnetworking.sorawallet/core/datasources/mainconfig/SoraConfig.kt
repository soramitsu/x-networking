package jp.co.soramitsu.xnetworking.sorawallet.core.datasources.mainconfig

data class SoraConfig(
    val remote: Boolean,
    val blockExplorerUrl: String,
    val blockExplorerType: ConfigExplorerType,
    val nodes: List<SoraConfigNode>,
    val genesis: String,
    val joinUrl: String,
    val substrateTypesUrl: String,
    val soracard: Boolean,
    val currencies: List<SoraCurrency>,
)

data class SoraConfigNode(
    val chain: String,
    val name: String,
    val address: String,
)

data class SoraCurrency(
    val code: String,
    val name: String,
    val sign: String,
)

data class ConfigExplorerType(
    val fiat: String,
    val reward: String,
    val sbapy: String,
    val assets: String,
)
