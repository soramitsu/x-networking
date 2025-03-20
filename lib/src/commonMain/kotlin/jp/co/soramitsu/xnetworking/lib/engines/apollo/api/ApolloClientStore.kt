package jp.co.soramitsu.xnetworking.lib.engines.apollo.api

import com.apollographql.apollo.api.Query
import io.mockative.Mockable

@Mockable
abstract class ApolloClientStore() {

    abstract suspend fun <Response: Query.Data> query(serverUrl: String, query: Query<Response>): Response

}