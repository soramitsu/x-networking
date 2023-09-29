package jp.co.soramitsu.xnetworking.basic.engines.apollo.api

import com.apollographql.apollo3.ApolloClient

interface ApolloClientStore {

    suspend fun addClient(tag: Int, serverUrl: String)

    suspend fun getClient(tag: Int): ApolloClient?

    companion object {

        const val SUBQUERY_TAG: Int = 0

    }

}