package jp.co.soramitsu.xnetworking.basic.datasources.txhistory.api

import jp.co.soramitsu.xnetworking.basic.datasources.txhistory.api.models.TxHistoryItem

interface HistoryItemsFilter {

    fun List<TxHistoryItem>.filterCachedHistoryItems(): List<TxHistoryItem>

    fun List<TxHistoryItem>.filterPagedHistoryItems(): List<TxHistoryItem>

}