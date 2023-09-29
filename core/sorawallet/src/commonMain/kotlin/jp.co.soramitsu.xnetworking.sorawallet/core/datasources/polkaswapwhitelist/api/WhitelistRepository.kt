package jp.co.soramitsu.xnetworking.sorawallet.core.datasources.polkaswapwhitelist.api

import jp.co.soramitsu.xnetworking.sorawallet.core.datasources.polkaswapwhitelist.api.AbstractWhitelistedToken


interface WhitelistRepository {

    suspend fun getWhitelistedTokens(requestUrl: String): List<AbstractWhitelistedToken>

    companion object {
        const val requestUrl = "https://whitelist.polkaswap2.io/whitelist.json"
    }

}