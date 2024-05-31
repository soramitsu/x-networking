package jp.co.soramitsu.xnetworking.sorawallet.txhistory.subquery.models

import jp.co.soramitsu.xnetworking.basic.common.ResponsePageInfo
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

@Serializable
data class SoraSubQueryResponse(
    @SerialName("data")
    val data: SoraSubQueryResponseData
)

@Serializable
data class SoraSubQueryResponseData(
    @SerialName("historyElements")
    val historyElements: HistoryResponseDataElements
)

@Serializable
data class HistoryResponseDataElements(
    @SerialName("nodes")
    val nodes: List<HistoryResponseItem>,
    @SerialName("pageInfo")
    val pageInfo: ResponsePageInfo,
)

@Serializable
data class HistoryResponseItem(
    @SerialName("id")
    val id: String,
    @SerialName("blockHash")
    val blockHash: String,
    @SerialName("module")
    val module: String,
    @SerialName("method")
    val method: String,
    @SerialName("timestamp")
    val timestamp: String,
    @SerialName("networkFee")
    val networkFee: String,
    @SerialName("execution")
    val execution: ExecutionResult,
    @SerialName("data")
    val data: JsonElement,
)

@Serializable
data class ExecutionResult(
    @SerialName("success")
    val success: Boolean,
    @SerialName("error")
    val error: Error? = null,
)

@Serializable
data class Error(
    @SerialName("moduleErrorId")
    val moduleErrorId: String? = null,
    @SerialName("moduleErrorIndex")
    val moduleErrorIndex: Int? = null,
    @SerialName("nonModuleErrorMessage")
    val nonModuleErrorMessage: String? = null
)
