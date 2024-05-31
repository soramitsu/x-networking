package jp.co.soramitsu.xnetworking.lib.datasources.txhistory.impl.domain.adapters.etherscan

import jp.co.soramitsu.xnetworking.lib.engines.utils.JsonGetRequest
import kotlinx.serialization.Serializable

@Suppress("FunctionName")
internal inline fun NormalEtherScanRequest(
    url: String,
    address: String,
    apiKey: String
) = JsonGetRequest(
    url = url,
    queryParams = mapOf(
        "module" to "account",
        "action" to "txlist",
        "address" to address,
        "page" to "1",
        "offset" to "1000",
        "sort" to "desc",
        "apiKey" to apiKey
    ),
    responseDeserializer = EtherScanResponse.serializer()
)

@Suppress("FunctionName")
internal inline fun ErcBepEtherScanRequest(
    url: String,
    contractAddress: String,
    address: String,
    apiKey: String
) = JsonGetRequest(
    url = url,
    queryParams = mapOf(
        "module" to "account",
        "action" to "tokentx",
        "contractAddress" to contractAddress,
        "address" to address,
        "page" to "1",
        "offset" to "1000",
        "sort" to "desc",
        "apiKey" to apiKey
    ),
    responseDeserializer = EtherScanResponse.serializer()
)

@Serializable
internal class EtherScanResponse(
    val status: Int,
    val message: String,
    val result: List<HistoryElement> = emptyList()
) {
    @Serializable
    class HistoryElement(
        val blockNumber: String,
        val timeStamp: Long,
        val hash: String,
        val nonce: String,
        val blockHash: String,
        val from: String,
        val to: String,
        val contractAddress: String,
        val value: String,
        val gas: String,
        val gasPrice: String,
        val gasUsed: String,
        val isError: Int
    )
}