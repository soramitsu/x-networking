package jp.co.soramitsu.xnetworking.sorawallet.core.datasources.polkaswapconfig.api.models.common

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CommonConfig(
    @SerialName("SUBQUERY_ENDPOINT")
    val subquery: String,
    @SerialName("DEFAULT_NETWORKS")
    val nodes: List<NodeInfo>,
    @SerialName("CHAIN_GENESIS_HASH")
    val genesis: String,
)