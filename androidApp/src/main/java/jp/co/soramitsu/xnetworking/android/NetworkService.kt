package jp.co.soramitsu.appxnetworking

import jp.co.soramitsu.xnetworking.android.ChainInfoConstants
import jp.co.soramitsu.xnetworking.lib.datasources.blockexplorer.api.BlockExplorerRepository
import jp.co.soramitsu.xnetworking.lib.datasources.blockexplorer.api.models.AssetInfo
import jp.co.soramitsu.xnetworking.lib.engines.rest.api.RestClient
import jp.co.soramitsu.xnetworking.lib.engines.utils.JsonGetRequest
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.ListSerializer
import java.util.concurrent.TimeUnit

class NetworkService(
    private val restClient: RestClient,
    private val blockExplorerRepository: BlockExplorerRepository
) {

    suspend fun getRequest() = restClient.get(
        request = JsonGetRequest(
            url = "https://www.github.com",
            responseDeserializer = ListSerializer(AssetRemote.serializer())
        )
    )

    suspend fun getAssets() = restClient.get(
        request = JsonGetRequest(
            url = "https://raw.githubusercontent.com/soramitsu/fearless-utils/android/v2/chains/assets.json",
            responseDeserializer = ListSerializer(AssetRemote.serializer())
        )
    )

    suspend fun getAssetsInfo(): List<AssetInfo> {
        val timeStampAsLong = TimeUnit.SECONDS.convert(System.currentTimeMillis(), TimeUnit.MILLISECONDS) - 24 * 60 * 60

        return blockExplorerRepository.getAssetsInfo(
            chainId = ChainInfoConstants.Sora.chainInfo.chainId,
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

    suspend fun getFiat() = blockExplorerRepository.getFiat(
        chainId = ChainInfoConstants.Sora.chainInfo.chainId
    )

    suspend fun getRewards() = blockExplorerRepository.getReferralReward(
        chainId = ChainInfoConstants.Sora.chainInfo.chainId,
        address = "cnVkoGs3rEMqLqY27c2nfVXJRGdzNJk2ns78DcqtppaSRe8qm",
    )

    suspend fun getApy() = blockExplorerRepository.getApy(
        chainId = ChainInfoConstants.Sora.chainInfo.chainId
    )

//    suspend fun getHistorySora(page: Long) =
//        txHistoryInteractor.getTransactionHistoryPaged(
//            address = "cnVkoGs3rEMqLqY27c2nfVXJRGdzNJk2ns78DcqtppaSRe8qm",
//            page = page,
//            requestUrl = "",
//            networkName = "fearless"
//        )

//    suspend fun getHistoryFearless(page: Long) =
//        txHistoryInteractor.getTransactionHistoryPaged(
//            address = "5ETrb47YCHE9pYxKfpm4b3bMNvKd7Zusi22yZLLHKadP5oYn",
//            networkName = "fearless",
//            page = page,
//            requestUrl = "https://api.subquery.network/sq/soramitsu/fearless-wallet-westend"
//        )

//    suspend fun getPeers(query: String) =
//        txHistoryInteractor.getTransactionPeers(query, "fearless")

//    suspend fun getSoraConfig(): SoraConfig? {
//        return soraConfigBuilder.getConfig()
//    }
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
