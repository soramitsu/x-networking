package jp.co.soramitsu.xnetworking.fearlesswallet.txhistory.subsquid.models

import jp.co.soramitsu.xnetworking.basic.common.ResponsePageInfo
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject

@Serializable
data class FearlessSubSquidResponse(
    @SerialName("data")
    val data: FearlessSubSquidResponseData
)

@Serializable
data class FearlessSubSquidResponseData(
    @SerialName("historyElementsConnection")
    val historyElements: FearlessHistorySubSquidResponseDataElements
)

@Serializable
data class FearlessHistorySubSquidResponseDataElements(
    @SerialName("edges")
    val nodes: List<FearlessSubSquidHistoryResponseItem>,
    @SerialName("pageInfo")
    val pageInfo: ResponsePageInfo,
)

@Serializable
data class FearlessSubSquidHistoryResponseItem(
    @SerialName("node")
    val node: FearlessSubSquidHistoryNode,
)

@Serializable
data class FearlessSubSquidHistoryNode(
    @SerialName("id")
    val id: String = "",
    @SerialName("timestamp")
    val timestamp: String = "",
    @SerialName("networkFee")
    val networkFee: String? = null,
    @SerialName("module")
    val module: String = "",
    @SerialName("method")
    val method: String = "",
    @SerialName("execution")
    val execution: ExecutionResult? = null,
    @SerialName("address")
    val address: String = "",
    @SerialName("blockHash")
    val blockHash: String = "",
    @SerialName("data")
    val data: JsonElement = JsonObject(emptyMap()),
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
