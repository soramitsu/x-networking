package jp.co.soramitsu.xnetworking.lib.datasources.txhistory.api.models

data class TxHistoryItemNested(
    val module: String,
    val method: String,
    val hash: String,
    val data: List<TxHistoryItemParam>
)