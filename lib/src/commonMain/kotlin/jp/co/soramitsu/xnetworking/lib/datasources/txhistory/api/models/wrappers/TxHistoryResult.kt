package jp.co.soramitsu.xnetworking.lib.datasources.txhistory.api.models.wrappers

data class TxHistoryResult<R>(
    val endCursor: String?,
    val endReached: Boolean,
    val page: Long,
    val items: List<R>,
    val errorMessage: String?,
)
