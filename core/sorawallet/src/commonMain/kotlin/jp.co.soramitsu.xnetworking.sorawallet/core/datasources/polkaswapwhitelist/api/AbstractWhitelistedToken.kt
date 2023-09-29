package jp.co.soramitsu.xnetworking.sorawallet.core.datasources.polkaswapwhitelist.api

abstract class AbstractWhitelistedToken {

    abstract fun getAddress(): String

    abstract fun getRawIcon(): String

    abstract fun getIconExtension(): String?

}