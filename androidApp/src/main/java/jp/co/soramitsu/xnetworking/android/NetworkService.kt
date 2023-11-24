package jp.co.soramitsu.xnetworking.android

import jp.co.soramitsu.xnetworking.basic.networkclient.SoramitsuNetworkClient
import jp.co.soramitsu.xnetworking.basic.txhistory.TxHistoryItem
import jp.co.soramitsu.xnetworking.fearlesswallet.chainbuilder.FearlessChainsBuilder
import jp.co.soramitsu.xnetworking.fearlesswallet.txhistory.client.SubQueryClientForFearlessWallet
import jp.co.soramitsu.xnetworking.sorawallet.blockexplorerinfo.SoraWalletBlockExplorerInfo
import jp.co.soramitsu.xnetworking.sorawallet.mainconfig.SoraConfig
import jp.co.soramitsu.xnetworking.sorawallet.mainconfig.SoraRemoteConfigBuilder
import jp.co.soramitsu.xnetworking.sorawallet.tokenwhitelist.SoraTokenWhitelistDto
import jp.co.soramitsu.xnetworking.sorawallet.tokenwhitelist.SoraTokensWhitelistManager
import jp.co.soramitsu.xnetworking.sorawallet.txhistory.client.SubQueryClientForSoraWallet
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.util.concurrent.TimeUnit

class NetworkService(
    private val client: SoramitsuNetworkClient,
    private val fearlessChainsBuilder: FearlessChainsBuilder,
    private val soraConfigBuilder: SoraRemoteConfigBuilder,
    private val subQueryClientForFearlessWallet: SubQueryClientForFearlessWallet,
    private val subQueryClientForSoraWallet: SubQueryClientForSoraWallet,
    private val soraWalletBlockExplorerInfo: SoraWalletBlockExplorerInfo,
    private val whitelistManager: SoraTokensWhitelistManager,
) {

    suspend fun getSoraWhitelist(): List<SoraTokenWhitelistDto> {
        return whitelistManager.getTokens()
    }

    suspend fun getFiat() = soraWalletBlockExplorerInfo.getFiat()

    suspend fun getAssets() =
        client.createJsonRequest<List<AssetRemote>>("https://raw.githubusercontent.com/soramitsu/fearless-utils/android/v2/chains/assets.json")

    suspend fun getRequest() = client.createJsonRequest<List<Int>>("https://www.github.com")

    suspend fun getChains() = fearlessChainsBuilder.getChains(
        "2.0.18",
        emptyList()
    )

    suspend fun getApy() = soraWalletBlockExplorerInfo.getSpApy()
    suspend fun getAssetsInfo() = soraWalletBlockExplorerInfo
        .getAssetsInfo(listOf(
            "0x0200000000000000000000000000000000000000000000000000000000000000",
            "0x0200040000000000000000000000000000000000000000000000000000000000",
            "0x0200050000000000000000000000000000000000000000000000000000000000",
            "0x0200060000000000000000000000000000000000000000000000000000000000",
            "0x0200070000000000000000000000000000000000000000000000000000000000",
            "0x0200080000000000000000000000000000000000000000000000000000000000",
            "0x0200090000000000000000000000000000000000000000000000000000000000",
        ), TimeUnit.SECONDS.convert(System.currentTimeMillis(), TimeUnit.MILLISECONDS) - 24 * 60 * 60)

    suspend fun getHistorySora(page: Long, f: (TxHistoryItem) -> Boolean) =
        subQueryClientForSoraWallet.getTransactionHistoryPaged(
            address = "cnVkoGs3rEMqLqY27c2nfVXJRGdzNJk2ns78DcqtppaSRe8qm",
            page = page,
            filter = f
        )

    suspend fun getHistoryFearless(page: Long, f: (TxHistoryItem) -> Boolean) =
        subQueryClientForFearlessWallet.getTransactionHistoryPaged(
            address = "5ETrb47YCHE9pYxKfpm4b3bMNvKd7Zusi22yZLLHKadP5oYn",
            networkName = "fearless",
            page = page,
            url = "https://api.subquery.network/sq/soramitsu/fearless-wallet-westend",
            filter = f,
        )

    suspend fun getHistoryFearlessSubsquid(page: Long, f: (TxHistoryItem) -> Boolean) =
        DepBuilder.subSquidClientForFearlessWallet.getTransactionHistoryPaged(
            "cnVkoGs3rEMqLqY27c2nfVXJRGdzNJk2ns78DcqtppaSRe8qm",
            "fearless",
            page,
            "https://squid.subsquid.io/sora/v/v4/graphql",
            f
        )

    suspend fun getPeers(query: String) =
        subQueryClientForSoraWallet.getTransactionPeers(query)

    suspend fun getRewards() = soraWalletBlockExplorerInfo.getReferrerRewards(
        address = "cnVkoGs3rEMqLqY27c2nfVXJRGdzNJk2ns78DcqtppaSRe8qm",
    )

    suspend fun getSoraConfig(): SoraConfig? = soraConfigBuilder.getConfig()
}

@Serializable
data class AssetRemote(
    @SerialName("id")
    val id: String?,
    @SerialName("chainId")
    val chainId: String?,
    @SerialName("precision")
    val precision: Int?,
    @SerialName("priceId")
    val priceId: String? = null,
    @SerialName("icon")
    val icon: String?,
)
