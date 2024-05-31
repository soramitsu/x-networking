package jp.co.soramitsu.xnetworking.basic.txhistory

class TxHistoryResult<R>(
    val endCursor: String?,
    val endReached: Boolean,
    val page: Long,
    val items: List<R>,
    val errorMessage: String?,
)
