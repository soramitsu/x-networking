package jp.co.soramitsu.xnetworking.basic.datasources.txhistory.api.models

data class TxHistoryItem(
    val id: String,
    val blockHash: String,
    val module: String,
    val method: String,
    val timestamp: String,
    val networkFee: String,
    val success: Boolean,
    val data: List<TxHistoryItemParam>?,
    val nestedData: List<TxHistoryItemNested>?
)