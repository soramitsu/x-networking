package jp.co.soramitsu.appxnetworking

import io.ktor.client.call.body
import jp.co.soramitsu.xnetworking.basic.engines.rest.api.RestClient
import jp.co.soramitsu.xnetworking.basic.engines.rest.api.models.AbstractRestServerRequest
import jp.co.soramitsu.xnetworking.basic.networkclient.SoramitsuNetworkClient
import jp.co.soramitsu.xnetworking.basic.txhistory.TxHistoryItem
import jp.co.soramitsu.xnetworking.fearlesswallet.chainbuilder.FearlessChainsBuilder
import jp.co.soramitsu.xnetworking.fearlesswallet.txhistory.client.SubQueryClientForFearlessWallet
import jp.co.soramitsu.xnetworking.sorawallet.common.interactors.blockexplorer.api.BlockExplorerInteractor
import jp.co.soramitsu.xnetworking.sorawallet.core.datasources.blockexplorer.api.models.AssetsInfoResponse
import jp.co.soramitsu.xnetworking.sorawallet.core.datasources.mainconfig.SoraConfig
import jp.co.soramitsu.xnetworking.sorawallet.core.datasources.mainconfig.SoraRemoteConfigBuilder
import jp.co.soramitsu.xnetworking.sorawallet.core.datasources.polkaswapwhitelist.api.AbstractWhitelistedToken
import jp.co.soramitsu.xnetworking.sorawallet.core.datasources.polkaswapwhitelist.api.WhitelistRepository
import jp.co.soramitsu.xnetworking.sorawallet.core.datasources.txhistory.client.SubQueryClientForSoraWallet
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.util.concurrent.TimeUnit

class NetworkService(
    private val client: SoramitsuNetworkClient,
    private val fearlessChainsBuilder: FearlessChainsBuilder,
    private val soraConfigBuilder: SoraRemoteConfigBuilder,
    private val subQueryClientForFearlessWallet: SubQueryClientForFearlessWallet,
    private val subQueryClientForSoraWallet: SubQueryClientForSoraWallet,
    private val restClient: RestClient,
    private val blockExplorerInteractor: BlockExplorerInteractor,
    private val whitelistRepository: WhitelistRepository,
) {

    suspend fun getRequest() = restClient.get(
        SimpleJSONGetRequestHolder(url = "https://www.github.com")
    ).body<List<Int>>()

    suspend fun getAssets() = restClient.get(
        SimpleJSONGetRequestHolder(
            url = "https://raw.githubusercontent.com/soramitsu/fearless-utils/android/v2/chains/assets.json"
        )
    ).body<List<AssetRemote>>()

    suspend fun getSoraWhitelist(): List<AbstractWhitelistedToken> {
        return whitelistRepository.getWhitelistedTokens(WhitelistRepository.requestUrl)
    }

    suspend fun getAssetsInfo(): List<AssetsInfoResponse> {
        val timeStampAsLong = TimeUnit.SECONDS.convert(System.currentTimeMillis(), TimeUnit.MILLISECONDS) - 24 * 60 * 60

        return blockExplorerInteractor
            .getAssetsInfo(
                tokenIds = listOf(
                    "0x0200000000000000000000000000000000000000000000000000000000000000",
                    "0x0200040000000000000000000000000000000000000000000000000000000000",
                    "0x0200050000000000000000000000000000000000000000000000000000000000",
                    "0x0200060000000000000000000000000000000000000000000000000000000000",
                    "0x0200070000000000000000000000000000000000000000000000000000000000",
                    "0x0200080000000000000000000000000000000000000000000000000000000000",
                    "0x0200090000000000000000000000000000000000000000000000000000000000",
                ),
                timeStamp = timeStampAsLong.toInt()
            )
    }

    suspend fun getFiat() = blockExplorerInteractor.getFiat()

    suspend fun getRewards() = blockExplorerInteractor.getReferrerRewards(
        address = "cnVkoGs3rEMqLqY27c2nfVXJRGdzNJk2ns78DcqtppaSRe8qm",
    )

    suspend fun getApy() = blockExplorerInteractor.getSbApyInfo()

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

    suspend fun getChains() = fearlessChainsBuilder.getChains(
        "2.0.18",
        emptyList()
    )

    suspend fun getPeers(query: String) =
        subQueryClientForSoraWallet.getTransactionPeers(query)

    suspend fun getSoraConfig(): SoraConfig? {
        return soraConfigBuilder.getConfig()
    }
}

private data class SimpleJSONGetRequestHolder(
    private val url: String
): AbstractRestServerRequest() {

    override fun getUrl(): String = url

    override fun getResponseContentType(): RestClient.ContentType = RestClient.ContentType.JSON
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
