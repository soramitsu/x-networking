package jp.co.soramitsu.commonnetworking.subquery.history

data class SoraHistoryInfo(
    val endReached: Boolean,
    val items: List<SoraHistoryItem>,
    val cursor: String?
)

data class SoraHistoryItem(
    val id: String,
    val blockHash: String,
    val module: String,
    val method: String,
    val timestamp: String,
    val networkFee: String,
    val success: Boolean,
    val data: List<SoraHistoryItemParam>?,
    val nestedData: List<SoraHistoryItemNested>?
)

data class SoraHistoryItemParam(
    val paramName: String,
    val paramValue: String,
)

data class SoraHistoryItemNested(
    val module: String,
    val method: String,
    val hash: String,
    val data: List<SoraHistoryItemParam>
)
