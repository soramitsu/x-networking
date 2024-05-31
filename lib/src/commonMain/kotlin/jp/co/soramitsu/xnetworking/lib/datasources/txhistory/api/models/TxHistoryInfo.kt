package jp.co.soramitsu.xnetworking.lib.datasources.txhistory.api.models

data class TxHistoryInfo(
    val endCursor: String?,
    val endReached: Boolean,
    val items: List<TxHistoryItem>
)