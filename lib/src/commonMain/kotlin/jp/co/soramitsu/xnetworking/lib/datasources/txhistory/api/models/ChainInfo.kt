package jp.co.soramitsu.xnetworking.lib.datasources.txhistory.api.models

sealed class ChainInfo {

    abstract val chainId: String

    class Simple(
        override val chainId: String
    ): ChainInfo()

    class Ethereum(
        override val chainId: String,
        val contractAddress: String,
        val ethereumType: String?,
        val apiKey: String
    ): ChainInfo()

    class OkLink(
        override val chainId: String,
        val symbol: String,
        val apiKey: String
    ): ChainInfo()

    class Zeta(
        override val chainId: String,
        val contractAddress: String,
        val ethereumType: String?
    ): ChainInfo()

}