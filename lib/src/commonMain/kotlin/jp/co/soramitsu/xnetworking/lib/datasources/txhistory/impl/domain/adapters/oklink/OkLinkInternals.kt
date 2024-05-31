package jp.co.soramitsu.xnetworking.lib.datasources.txhistory.impl.domain.adapters.oklink

import jp.co.soramitsu.xnetworking.lib.engines.utils.JsonGetRequest
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Suppress("FunctionName")
internal inline fun OkLinkRequest(
    url: String,
    address: String,
    symbol: String,
    apiKey: String
) = JsonGetRequest(
    url = url,
    headers = mapOf(
        "Ok-Access-Key" to apiKey
    ),
    queryParams = mapOf(
        "address" to address,
        "symbol" to symbol
    ),
    responseDeserializer = OkLinkResponse.serializer()
)

@Serializable
internal class OkLinkResponse(
    val code: Int,
    val msg: String,
    val data: List<HistoryPage> = emptyList()
) {
    @Serializable
    class HistoryPage(
        val page: Int,
        val limit: Int,
        val totalPage: String,
        @SerialName("transactionLists")
        val items: List<HistoryItem> = emptyList()
    ) {
        @Serializable
        class HistoryItem(
            val txId: String,
            val methodId: String,
            val blockHash: String,
            val height: String,
            val transactionTime: Long,
            val from: String,
            val to: String,
            val isFromContract: Boolean,
            val isToContract: Boolean,
            val amount: String,
            val transactionSymbol: String,
            val txFee: String,
            val state: String,
            val tokenId: String,
            val tokenContractAddress: String,
            val challengeStatus: String,
            val l1OriginHash: String
        )
    }
}