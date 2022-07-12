package jp.co.soramitsu.commonnetworking.subquery.history

data class SubQueryHistoryInfo(
    val endCursor: String?,
    val endReached: Boolean,
    val items: List<SubQueryHistoryItem>
)

data class SubQueryHistoryItem(
    val id: String,
    val blockHash: String,
    val module: String,
    val method: String,
    val timestamp: String,
    val networkFee: String,
    val success: Boolean,
    val data: List<SubQueryHistoryItemParam>?,
    val nestedData: List<SubQueryHistoryItemNested>?
)

data class SubQueryHistoryItemParam(
    val paramName: String,
    val paramValue: String,
)

data class SubQueryHistoryItemNested(
    val module: String,
    val method: String,
    val hash: String,
    val data: List<SubQueryHistoryItemParam>
)
