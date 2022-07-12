package jp.co.soramitsu.commonnetworking.subquery.history.sora

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

@Serializable
data class SoraSubqueryResponse(
    val data: SoraSubqueryResponseData
)

@Serializable
data class SoraSubqueryResponseData(
    val historyElements: HistoryResponseDataElements
)

@Serializable
data class HistoryResponseDataElements(
    val nodes: List<HistoryResponseItem>, val pageInfo: HistoryResponsePageInfo
)

@Serializable
data class HistoryResponsePageInfo(
    val endCursor: String?,
    val hasNextPage: Boolean,
)

@Serializable
data class HistoryResponseItem(
    val id: String,
    val blockHash: String,
    val module: String,
    val method: String,
    val timestamp: String,
    val networkFee: String,
    val execution: ExecutionResult,
    val data: JsonElement
)

@Serializable
data class ExecutionResult(
    val success: Boolean,
    val error: Error? = null
)

@Serializable
data class Error(
    val moduleErrorId: String? = null,
    val moduleErrorIndex: Int? = null,
    val nonModuleErrorMessage: String? = null
)
