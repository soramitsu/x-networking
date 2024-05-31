package jp.co.soramitsu.xnetworking.lib.datasources.txhistory.api.models

sealed class ChainInfo {

    abstract val chainId: String

    class Simple(
        override val chainId: String
    ): ChainInfo()

    class WithEthereumType(
        override val chainId: String,
        val contractAddress: String,
        val ethereumType: String?
    ): ChainInfo()

    class WithAssetSymbol(
        override val chainId: String,
        val symbol: String
    ): ChainInfo()

}