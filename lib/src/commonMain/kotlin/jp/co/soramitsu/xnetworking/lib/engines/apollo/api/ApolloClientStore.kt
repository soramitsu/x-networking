package jp.co.soramitsu.xnetworking.lib.engines.apollo.api

import com.apollographql.apollo3.api.Query

abstract class ApolloClientStore() {

    abstract suspend fun <Response: Query.Data> query(serverUrl: String, query: Query<Response>): Response

}