package jp.co.soramitsu.xnetworking.basic.engines.apollo.impl

import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.api.ApolloRequest
import com.apollographql.apollo3.api.ApolloResponse
import com.apollographql.apollo3.api.Operation
import com.apollographql.apollo3.api.http.HttpMethod
import com.apollographql.apollo3.network.http.LoggingInterceptor
import jp.co.soramitsu.xnetworking.basic.engines.apollo.api.ApolloClientStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class ApolloClientStoreImpl: ApolloClientStore {

    private val clientsMapMutex = Mutex()

    private val clientsMap = mutableMapOf<Int, ApolloClient>()

    override suspend fun addClient(tag: Int, serverUrl: String) {
        clientsMapMutex.withLock {
            clientsMap.getOrPut(tag) {
                ApolloClient.Builder()
                    .serverUrl(serverUrl)
                    .build()
            }
        }
    }

    override suspend fun getClient(tag: Int): ApolloClient? {
        return clientsMapMutex.withLock {
            clientsMap.getOrElse(tag) {
                null
            }
        }
    }

}