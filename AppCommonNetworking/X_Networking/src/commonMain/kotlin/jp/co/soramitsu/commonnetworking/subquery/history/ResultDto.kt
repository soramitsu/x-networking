package jp.co.soramitsu.commonnetworking.subquery.history

class SubQueryHistoryResult<R>(
    val endCursor: String?,
    val endReached: Boolean,
    val page: Long,
    val items: List<R>
)