package jp.co.soramitsu.xnetworking.basic.datasources.txhistory.api.wrappers

data class TxHistoryResult<R>(
    val endCursor: String?,
    val endReached: Boolean,
    val page: Long,
    val items: List<R>,
    val errorMessage: String?,
)
