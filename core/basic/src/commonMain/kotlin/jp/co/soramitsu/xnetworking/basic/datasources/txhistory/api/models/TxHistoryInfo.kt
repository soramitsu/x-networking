package jp.co.soramitsu.xnetworking.basic.datasources.txhistory.api.models

data class TxHistoryInfo(
    val endCursor: String?,
    val endReached: Boolean,
    val items: List<TxHistoryItem>
)