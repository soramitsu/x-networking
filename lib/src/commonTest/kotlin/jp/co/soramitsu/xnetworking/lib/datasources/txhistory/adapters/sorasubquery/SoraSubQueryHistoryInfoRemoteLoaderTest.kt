package jp.co.soramitsu.xnetworking.lib.datasources.txhistory.adapters.sorasubquery

import com.apollographql.apollo.api.Optional
import com.apollographql.apollo.api.Query
import jp.co.soramitsu.xnetworking.lib.datasources.chainsconfig.api.ConfigDAO
import jp.co.soramitsu.xnetworking.lib.datasources.chainsconfig.api.models.ExternalApiDAOException
import jp.co.soramitsu.xnetworking.lib.datasources.chainsconfig.api.models.ExternalApiType
import jp.co.soramitsu.xnetworking.lib.datasources.chainsconfig.api.models.StakingOption
import jp.co.soramitsu.xnetworking.lib.datasources.txhistory.api.adapters.HistoryInfoRemoteLoader
import jp.co.soramitsu.xnetworking.lib.datasources.txhistory.api.models.ChainInfo
import jp.co.soramitsu.xnetworking.lib.datasources.txhistory.api.models.TxFilter
import jp.co.soramitsu.xnetworking.lib.datasources.txhistory.api.models.TxHistoryInfo
import jp.co.soramitsu.xnetworking.lib.datasources.txhistory.api.models.TxHistoryItem
import jp.co.soramitsu.xnetworking.lib.datasources.txhistory.api.models.TxHistoryItemNested
import jp.co.soramitsu.xnetworking.lib.datasources.txhistory.api.models.TxHistoryItemParam
import jp.co.soramitsu.xnetworking.lib.datasources.txhistory.impl.domain.adapters.sorasubquery.SoraSubQueryHistoryInfoRemoteLoader
import jp.co.soramitsu.xnetworking.lib.engines.apollo.api.ApolloClientStore
import jp.co.soramitsu.xnetworking.mainnet.GetMainnetHistoryElementsQuery
import jp.co.soramitsu.xnetworking.mainnet.type.HistoryElementsOrderBy
import jp.co.soramitsu.xnetworking.mainnet.type.buildHistoryElement
import jp.co.soramitsu.xnetworking.mainnet.type.buildHistoryElementsConnection
import jp.co.soramitsu.xnetworking.mainnet.type.buildPageInfo
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlin.test.Test
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

private class FakeConfigDao(
    private val historyUrl: String?
) : ConfigDAO() {
    override suspend fun historyType(chainId: String): ExternalApiType {
        TODO("Not yet implemented")
    }

    override suspend fun historyUrl(chainId: String): String {
        return historyUrl ?: throw ExternalApiDAOException.NullUrl(chainId)
    }

    override suspend fun stakingType(chainId: String): ExternalApiType {
        TODO("Not yet implemented")
    }

    override suspend fun stakingUrl(chainId: String): String {
        TODO("Not yet implemented")
    }

    override suspend fun staking(chainId: String): StakingOption? {
        TODO("Not yet implemented")
    }
}

private class FakeApolloClientStore(
    private val response: GetMainnetHistoryElementsQuery.Data? = null
) : ApolloClientStore() {
    override suspend fun <Response : Query.Data> query(
        serverUrl: String,
        query: Query<Response>
    ): Response {
        return response as Response
    }
}

class SoraSubQueryHistoryInfoRemoteLoaderTest {

    private companion object {
        const val chainId = "sora"
        const val requestUrl = "sora.url"

        const val cursor = ""
        const val pageCount = 0
        const val signAddress = ""
    }

    @Test
    fun `TEST soraSubQueryHistoryInfoRemoteLoader_loadHistoryInfo EXPECT ExternalApiDAOException_NullUrl BECAUSE history url is null`() =
        runTest {
            // Test Data Start
            val filters = TxFilter.entries.toSet()

            val historyInfoRemoteLoader: HistoryInfoRemoteLoader =
                SoraSubQueryHistoryInfoRemoteLoader(
                    configDAO = FakeConfigDao(null),
                    apolloClientStore = FakeApolloClientStore()
                )

            val soraRequestToMock =
                GetMainnetHistoryElementsQuery(
                    pageCount = Optional.present(pageCount),
                    cursor = Optional.present(cursor),
                    orderBy = Optional.present(listOf(jp.co.soramitsu.xnetworking.mainnet.type.HistoryElementsOrderBy.TIMESTAMP_DESC)),
                    filter = Optional.present(
                        SoraSubQueryHistoryInfoRemoteLoader.createHistoryElementsFilter(
                            signAddress
                        )
                    )
                )
            // Test Data End

            assertFailsWith<ExternalApiDAOException.NullUrl> {
                historyInfoRemoteLoader.loadHistoryInfo(
                    pageCount = pageCount,
                    cursor = cursor,
                    signAddress = signAddress,
                    chainInfo = ChainInfo.Simple(
                        chainId = chainId
                    ),
                    filters = filters
                )
            }

            // Verification & Assertion
//            coVerify {
//                apolloClientStore.query(
//                    serverUrl = requestUrl,
//                    query = soraRequestToMock
//                )
//            }.wasNotInvoked()
        }

    @Test
    fun `TEST soraSubQueryHistoryInfoRemoteLoader_loadHistoryInfo EXPECT IllegalStateException BECAUSE historyElementConnections are null`() =
        runTest {
            // Test Data Start
            val filters = TxFilter.entries.toSet()

            val soraRequestToMock =
                GetMainnetHistoryElementsQuery(
                    pageCount = Optional.present(pageCount),
                    cursor = Optional.present(cursor),
                    orderBy = Optional.present(listOf(HistoryElementsOrderBy.TIMESTAMP_DESC)),
                    filter = Optional.present(
                        SoraSubQueryHistoryInfoRemoteLoader.createHistoryElementsFilter(
                            signAddress
                        )
                    )
                )

            val soraResponseToReturn =
                GetMainnetHistoryElementsQuery.Data {
                    historyElements = null
                }

            val historyInfoRemoteLoader: HistoryInfoRemoteLoader =
                SoraSubQueryHistoryInfoRemoteLoader(
                    configDAO = FakeConfigDao(requestUrl),
                    apolloClientStore = FakeApolloClientStore(soraResponseToReturn)
                )
            // Test Data End

            assertFailsWith<IllegalStateException> {
                historyInfoRemoteLoader.loadHistoryInfo(
                    pageCount = pageCount,
                    cursor = cursor,
                    signAddress = signAddress,
                    chainInfo = ChainInfo.Simple(
                        chainId = chainId
                    ),
                    filters = filters
                )
            }

            // Verification & Assertion
//            coVerify {
//                apolloClientStore.query(
//                    serverUrl = requestUrl,
//                    query = soraRequestToMock
//                )
//            }.wasInvoked(1)
        }

    @Test
    fun `TEST soraSubQueryHistoryInfoRemoteLoader_loadHistoryInfo EXPECT emptyList FOR empty filters set`() =
        runTest {
            // Test Data Start
            val filters = emptySet<TxFilter>()

            val soraRequestToMock =
                GetMainnetHistoryElementsQuery(
                    pageCount = Optional.present(pageCount),
                    cursor = Optional.present(cursor),
                    orderBy = Optional.present(listOf(HistoryElementsOrderBy.TIMESTAMP_DESC)),
                    filter = Optional.present(
                        SoraSubQueryHistoryInfoRemoteLoader.createHistoryElementsFilter(
                            signAddress
                        )
                    )
                )

            val soraResponseToReturn =
                GetMainnetHistoryElementsQuery.Data {
                    historyElements = buildHistoryElementsConnection {
                        nodes = listOf(
                            buildHistoryElement {
                                id = "id_123"
                                blockHash = "blockHash_123"
                                module = "module_123"
                                method = "method_123"
                                address = "address_123"
                                networkFee = "fee_123"
                                execution = JsonObject(
                                    content = mapOf(
                                        "success" to JsonPrimitive(true)
                                    )
                                )
                                timestamp = 123
                                data = JsonArray(
                                    content = listOf(
                                        JsonObject(
                                            content = mapOf(
                                                "hash" to JsonPrimitive("hash_246"),
                                                "module" to JsonPrimitive("module_246"),
                                                "method" to JsonPrimitive("method_246"),
                                                "data" to JsonObject(
                                                    content = mapOf(
                                                        "args" to JsonObject(
                                                            content = mapOf(
                                                                "key_246" to JsonPrimitive("value_246")
                                                            )
                                                        )
                                                    )
                                                )
                                            )
                                        )
                                    )
                                )
                            }
                        )
                        pageInfo = buildPageInfo {
                            endCursor = "endCursor_123"
                            hasNextPage = false
                        }
                    }
                }

            val historyInfoRemoteLoader: HistoryInfoRemoteLoader =
                SoraSubQueryHistoryInfoRemoteLoader(
                    configDAO = FakeConfigDao(requestUrl),
                    apolloClientStore = FakeApolloClientStore(soraResponseToReturn)
                )

            val expectedResult = TxHistoryInfo(
                endCursor = cursor,
                endReached = false,
                items = emptyList()
            )
            // Test Data End

            val result = historyInfoRemoteLoader.loadHistoryInfo(
                pageCount = pageCount,
                cursor = cursor,
                signAddress = signAddress,
                chainInfo = ChainInfo.Simple(
                    chainId = chainId
                ),
                filters = filters
            )

            // Verification & Assertion
//            coVerify {
//                apolloClientStore.query(
//                    serverUrl = requestUrl,
//                    query = soraRequestToMock
//                )
//            }.wasNotInvoked()

            assertTrue { result == expectedResult }
        }

    @Test
    fun `TEST soraSubQueryHistoryInfoRemoteLoader_loadHistoryInfo EXPECT success`() =
        runTest {
            // Test Data Start
            val filters = TxFilter.entries.toSet()

            val soraRequestToMock =
                GetMainnetHistoryElementsQuery(
                    pageCount = Optional.present(pageCount),
                    cursor = Optional.present(cursor),
                    orderBy = Optional.present(listOf(HistoryElementsOrderBy.TIMESTAMP_DESC)),
                    filter = Optional.present(
                        SoraSubQueryHistoryInfoRemoteLoader.createHistoryElementsFilter(
                            signAddress
                        )
                    )
                )

            val soraResponseToReturn =
                GetMainnetHistoryElementsQuery.Data {
                    historyElements = buildHistoryElementsConnection {
                        nodes = listOf(
                            buildHistoryElement {
                                id = "id_123"
                                blockHash = "blockHash_123"
                                module = "module_123"
                                method = "method_123"
                                address = "address_123"
                                networkFee = "fee_123"
                                execution = JsonObject(
                                    content = mapOf(
                                        "success" to JsonPrimitive(true)
                                    )
                                )
                                timestamp = 123
                                data = JsonArray(
                                    content = listOf(
                                        JsonObject(
                                            content = mapOf(
                                                "hash" to JsonPrimitive("hash_246"),
                                                "module" to JsonPrimitive("module_246"),
                                                "method" to JsonPrimitive("method_246"),
                                                "data" to JsonObject(
                                                    content = mapOf(
                                                        "args" to JsonObject(
                                                            content = mapOf(
                                                                "key_246" to JsonPrimitive("value_246")
                                                            )
                                                        )
                                                    )
                                                )
                                            )
                                        )
                                    )
                                )
                            }
                        )
                        pageInfo = buildPageInfo {
                            endCursor = "endCursor_123"
                            hasNextPage = false
                        }
                    }
                }

            val expectedResult = TxHistoryInfo(
                endCursor = "endCursor_123",
                endReached = true,
                items = listOf(
                    TxHistoryItem(
                        id = "id_123",
                        blockHash = "blockHash_123",
                        module = "module_123",
                        method = "method_123",
                        timestamp = "123",
                        nestedData = listOf(
                            TxHistoryItemNested(
                                hash = "hash_246",
                                module = "module_246",
                                method = "method_246",
                                data = listOf(
                                    TxHistoryItemParam(
                                        "key_246",
                                        "value_246"
                                    )
                                )
                            )
                        ),
                        networkFee = "fee_123",
                        success = true,
                        data = null
                    )
                )
            )

            val historyInfoRemoteLoader: HistoryInfoRemoteLoader =
                SoraSubQueryHistoryInfoRemoteLoader(
                    configDAO = FakeConfigDao(requestUrl),
                    apolloClientStore = FakeApolloClientStore(soraResponseToReturn)
                )
            // Test Data End

            val result = historyInfoRemoteLoader.loadHistoryInfo(
                pageCount = pageCount,
                cursor = cursor,
                signAddress = signAddress,
                chainInfo = ChainInfo.Simple(
                    chainId = chainId
                ),
                filters = filters
            )

            // Verification & Assertion
//            coVerify {
//                apolloClientStore.query(
//                    serverUrl = requestUrl,
//                    query = soraRequestToMock
//                )
//            }.wasInvoked(1)

            assertTrue { result == expectedResult }
        }

}