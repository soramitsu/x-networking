package jp.co.soramitsu.xnetworking.sorawallet.core.datasources.blockexplorer.impl.rest.assets

data class AssetsInfo(
    val tokenId: String,
    val liquidity: String,
    val hourDelta: Double?,
)
