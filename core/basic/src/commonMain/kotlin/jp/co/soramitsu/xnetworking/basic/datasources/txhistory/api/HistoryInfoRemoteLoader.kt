package jp.co.soramitsu.xnetworking.basic.datasources.txhistory.api

import com.apollographql.apollo3.exception.ApolloException
import jp.co.soramitsu.xnetworking.basic.datasources.txhistory.api.models.TxHistoryInfo

interface HistoryInfoRemoteLoader {

    @Throws(
        ApolloException::class,
        IllegalStateException::class
    )
    suspend fun loadHistoryInfo(
        pageCount: Int,
        cursor: String,
        signAddress: String
    ): TxHistoryInfo

}