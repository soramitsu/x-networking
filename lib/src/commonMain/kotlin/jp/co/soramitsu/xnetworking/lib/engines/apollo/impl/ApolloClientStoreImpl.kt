package jp.co.soramitsu.xnetworking.lib.engines.apollo.impl

import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.api.Query
import jp.co.soramitsu.xnetworking.lib.engines.apollo.api.ApolloClientStore
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class ApolloClientStoreImpl: ApolloClientStore() {

    private val mutex = Mutex()

    private val apolloClientsByUrl = mutableMapOf<String, ApolloClient>()

    override suspend fun <Response : Query.Data> query(
        serverUrl: String,
        query: Query<Response>
    ): Response {
        if (serverUrl !in apolloClientsByUrl.keys) {
            mutex.withLock {
                if (serverUrl !in apolloClientsByUrl.keys) {
                    apolloClientsByUrl[serverUrl] = ApolloClient.Builder()
                        .serverUrl(serverUrl)
                        .build()
                }
            }
        }

        return apolloClientsByUrl[serverUrl]!!.query(query).execute().dataAssertNoErrors
    }

}