package jp.co.soramitsu.xnetworking.lib.datasources.txhistory.adapters.westend

import com.apollographql.apollo3.api.Optional
import io.mockative.Mock
import io.mockative.classOf
import io.mockative.coEvery
import io.mockative.coVerify
import io.mockative.mock
import jp.co.soramitsu.xnetworking.fearlesswallet.GetFearlessHistoryElementsQuery
import jp.co.soramitsu.xnetworking.fearlesswallet.type.HistoryElementsOrderBy
import jp.co.soramitsu.xnetworking.fearlesswallet.type.buildHistoryElementsConnection
import jp.co.soramitsu.xnetworking.fearlesswallet.type.buildPageInfo
import jp.co.soramitsu.xnetworking.lib.datasources.chainsconfig.api.ConfigDAO
import jp.co.soramitsu.xnetworking.lib.datasources.chainsconfig.api.models.ExternalApiDAOException
import jp.co.soramitsu.xnetworking.lib.datasources.txhistory.api.models.ChainInfo
import jp.co.soramitsu.xnetworking.lib.datasources.txhistory.api.adapters.HistoryInfoRemoteLoader
import jp.co.soramitsu.xnetworking.lib.datasources.txhistory.api.models.TxFilter
import jp.co.soramitsu.xnetworking.lib.datasources.txhistory.api.models.TxHistoryInfo
import jp.co.soramitsu.xnetworking.lib.datasources.txhistory.api.models.TxHistoryItem
import jp.co.soramitsu.xnetworking.lib.datasources.txhistory.api.models.TxHistoryItemParam
import jp.co.soramitsu.xnetworking.lib.datasources.txhistory.impl.domain.adapters.westend.WestendHistoryInfoRemoteLoader
import jp.co.soramitsu.xnetworking.lib.engines.apollo.api.ApolloClientStore
import jp.co.soramitsu.xnetworking.fearlesswallet.type.buildHistoryElement
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlin.test.Test
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class WestendHistoryInfoRemoteLoaderTest {

    private companion object {
        const val chainId = "westend"
        const val requestUrl = "westend.url"

        const val cursor = ""
        const val pageCount = 0
        const val signAddress = ""
    }

    @Mock
    private val configDAO = mock(classOf<ConfigDAO>())

    @Mock
    private val apolloClientStore = mock(classOf<ApolloClientStore>())

    private val historyInfoRemoteLoader: HistoryInfoRemoteLoader =
        WestendHistoryInfoRemoteLoader(
            configDAO = configDAO,
            apolloClientStore = apolloClientStore
        )

    @Test
    fun `TEST westendHistoryInfoRemoteLoader_loadHistoryInfo EXPECT ExternalApiDAOException_NullUrl BECAUSE history url is null`() =
        runTest {
            // Test Data Start
            val filters = TxFilter.entries.toSet()

            val westendRequestToMock =
                GetFearlessHistoryElementsQuery(
                    pageCount = pageCount,
                    address = signAddress,
                    cursor = cursor,
                    orderBy = Optional.present(listOf(HistoryElementsOrderBy.TIMESTAMP_DESC))
                )
            // Test Data End

            // Mocks Preparation Start
            coEvery {
                configDAO.historyUrl(
                    chainId = chainId
                )
            }.throws(ExternalApiDAOException.NullUrl(chainId))
            // Mocks Preparation End

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
            coVerify {
                apolloClientStore.query(
                    serverUrl = requestUrl,
                    query = westendRequestToMock
                )
            }.wasNotInvoked()
        }

    @Test
    fun `TEST westendHistoryInfoRemoteLoader_loadHistoryInfo EXPECT IllegalStateException BECAUSE historyElementConnections are null`() =
        runTest {
            // Test Data Start
            val filters = TxFilter.entries.toSet()

            val westendRequestToMock =
                GetFearlessHistoryElementsQuery(
                    pageCount = pageCount,
                    address = signAddress,
                    cursor = cursor,
                    orderBy = Optional.present(listOf(HistoryElementsOrderBy.TIMESTAMP_DESC))
                )

            val westendResponseToReturn =
                GetFearlessHistoryElementsQuery.Data {
                    historyElements = null
                }
            // Test Data End

            // Mocks Preparation Start
            coEvery {
                configDAO.historyUrl(
                    chainId = chainId
                )
            }.returns(requestUrl)

            coEvery {
                apolloClientStore.query(
                    serverUrl = requestUrl,
                    query = westendRequestToMock
                )
            }.returns(westendResponseToReturn)
            // Mocks Preparation End

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
            coVerify {
                apolloClientStore.query(
                    serverUrl = requestUrl,
                    query = westendRequestToMock
                )
            }.wasInvoked(1)
        }

    @Test
    fun `TEST westendHistoryInfoRemoteLoader_loadHistoryInfo EXPECT emptyList FOR empty filters set`() =
        runTest {
            // Test Data Start
            val filters = emptySet<TxFilter>()

            val westendRequestToMock =
                GetFearlessHistoryElementsQuery(
                    pageCount = pageCount,
                    address = signAddress,
                    cursor = cursor,
                    orderBy = Optional.present(listOf(HistoryElementsOrderBy.TIMESTAMP_DESC))
                )

            val westendResponseToReturn =
                GetFearlessHistoryElementsQuery.Data {
                    historyElements = buildHistoryElementsConnection {
                        nodes = listOf(
                            buildHistoryElement {
                                id = "id_123"
                                timestamp = "timestamp_123"
                                address = "address_someone"
                                reward = JsonObject(
                                    mapOf(
                                        "amount" to JsonPrimitive("amount_123"),
                                        "era" to JsonPrimitive("era_123"),
                                        "isReward" to JsonPrimitive("true"),
                                        "validator" to JsonPrimitive("validator_123")
                                    )
                                )
                                transfer = JsonObject(
                                    mapOf(
                                        "block" to JsonPrimitive("block_123"),
                                        "amount" to JsonPrimitive("amount_123"),
                                        "to" to JsonPrimitive("to_someone"),
                                        "from" to JsonPrimitive("from_someone"),
                                        "extrinsicHash" to JsonPrimitive("extrinsicHash_123"),
                                        "fee" to JsonPrimitive("fee_123"),
                                        "success" to JsonPrimitive("true")
                                    )
                                )
                                extrinsic = JsonObject(
                                    mapOf(
                                        "call" to JsonPrimitive("call_123"),
                                        "hash" to JsonPrimitive("hash_123"),
                                        "module" to JsonPrimitive("module_123"),
                                        "fee" to JsonPrimitive("fee_123"),
                                        "success" to JsonPrimitive("true")
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
                endCursor = cursor,
                endReached = false,
                items = emptyList()
            )
            // Test Data End

            // Mocks Preparation Start
            coEvery {
                configDAO.historyUrl(
                    chainId = chainId
                )
            }.returns(requestUrl)

            coEvery {
                apolloClientStore.query(
                    serverUrl = requestUrl,
                    query = westendRequestToMock
                )
            }.returns(westendResponseToReturn)
            // Mocks Preparation End

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
            coVerify {
                apolloClientStore.query(
                    serverUrl = requestUrl,
                    query = westendRequestToMock
                )
            }.wasNotInvoked()

            assertTrue { result == expectedResult }
        }

    @Test
    fun `TEST westendHistoryInfoRemoteLoader_loadHistoryInfo EXPECT success FOR transfers AND transfer filter set`() =
        runTest {
            // Test Data Start
            val filters = setOf(TxFilter.TRANSFER)

            val westendRequestToMock =
                GetFearlessHistoryElementsQuery(
                    pageCount = pageCount,
                    address = signAddress,
                    cursor = cursor,
                    orderBy = Optional.present(listOf(HistoryElementsOrderBy.TIMESTAMP_DESC))
                )

            val westendResponseToReturn =
                GetFearlessHistoryElementsQuery.Data {
                    historyElements = buildHistoryElementsConnection {
                        nodes = listOf(
                            buildHistoryElement {
                                id = "id_123"
                                timestamp = "timestamp_123"
                                address = "address_someone"
                                reward = JsonObject(
                                    mapOf(
                                        "amount" to JsonPrimitive("amount_123"),
                                        "era" to JsonPrimitive("era_123"),
                                        "isReward" to JsonPrimitive("true"),
                                        "validator" to JsonPrimitive("validator_123")
                                    )
                                )
                                transfer = JsonObject(
                                    mapOf(
                                        "block" to JsonPrimitive("block_123"),
                                        "amount" to JsonPrimitive("amount_123"),
                                        "to" to JsonPrimitive("to_someone"),
                                        "from" to JsonPrimitive("from_someone"),
                                        "extrinsicHash" to JsonPrimitive("extrinsicHash_123"),
                                        "fee" to JsonPrimitive("fee_123"),
                                        "success" to JsonPrimitive("true")
                                    )
                                )
                                extrinsic = JsonObject(
                                    mapOf(
                                        "call" to JsonPrimitive("call_123"),
                                        "hash" to JsonPrimitive("hash_123"),
                                        "module" to JsonPrimitive("module_123"),
                                        "fee" to JsonPrimitive("fee_123"),
                                        "success" to JsonPrimitive("true")
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
                        blockHash = "",
                        module = "transfer",
                        method = "",
                        timestamp = "timestamp_123",
                        nestedData = null,
                        networkFee = "fee_123",
                        success = true,
                        data = listOfNotNull(
                            TxHistoryItemParam(
                                "block",
                                "block_123"
                            ),
                            TxHistoryItemParam(
                                "amount",
                                "amount_123"
                            ),
                            TxHistoryItemParam(
                                "to",
                                "to_someone"
                            ),
                            TxHistoryItemParam(
                                "from",
                                "from_someone"
                            ),
                            TxHistoryItemParam(
                                "extrinsicHash",
                                "extrinsicHash_123"
                            )
                        )
                    )
                )
            )
            // Test Data End

            // Mocks Preparation Start
            coEvery {
                configDAO.historyUrl(
                    chainId = chainId
                )
            }.returns(requestUrl)

            coEvery {
                apolloClientStore.query(
                    serverUrl = requestUrl,
                    query = westendRequestToMock
                )
            }.returns(westendResponseToReturn)
            // Mocks Preparation End

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
            coVerify {
                apolloClientStore.query(
                    serverUrl = requestUrl,
                    query = westendRequestToMock
                )
            }.wasInvoked(1)

            assertTrue { result == expectedResult }
        }

    @Test
    fun `TEST westendHistoryInfoRemoteLoader_loadHistoryInfo EXPECT success FOR rewards AND reward filter set`() =
        runTest {
            // Test Data Start
            val filters = setOf(TxFilter.REWARD)

            val westendRequestToMock =
                GetFearlessHistoryElementsQuery(
                    pageCount = pageCount,
                    address = signAddress,
                    cursor = cursor,
                    orderBy = Optional.present(listOf(HistoryElementsOrderBy.TIMESTAMP_DESC))
                )

            val westendResponseToReturn =
                GetFearlessHistoryElementsQuery.Data {
                    historyElements = buildHistoryElementsConnection {
                        nodes = listOf(
                            buildHistoryElement {
                                id = "id_123"
                                timestamp = "timestamp_123"
                                address = "address_someone"
                                reward = JsonObject(
                                    mapOf(
                                        "amount" to JsonPrimitive("amount_123"),
                                        "era" to JsonPrimitive("era_123"),
                                        "isReward" to JsonPrimitive("true"),
                                        "validator" to JsonPrimitive("validator_123")
                                    )
                                )
                                transfer = JsonObject(
                                    mapOf(
                                        "block" to JsonPrimitive("block_123"),
                                        "amount" to JsonPrimitive("amount_123"),
                                        "to" to JsonPrimitive("to_someone"),
                                        "from" to JsonPrimitive("from_someone"),
                                        "extrinsicHash" to JsonPrimitive("extrinsicHash_123"),
                                        "fee" to JsonPrimitive("fee_123"),
                                        "success" to JsonPrimitive("true")
                                    )
                                )
                                extrinsic = JsonObject(
                                    mapOf(
                                        "call" to JsonPrimitive("call_123"),
                                        "hash" to JsonPrimitive("hash_123"),
                                        "module" to JsonPrimitive("module_123"),
                                        "fee" to JsonPrimitive("fee_123"),
                                        "success" to JsonPrimitive("true")
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
                        blockHash = "",
                        module = "reward",
                        method = "",
                        timestamp = "timestamp_123",
                        nestedData = null,
                        networkFee = "0",
                        success = true,
                        data = listOfNotNull(
                            TxHistoryItemParam(
                                "amount",
                                "amount_123"
                            ),
                            TxHistoryItemParam(
                                "era",
                                "era_123"
                            ),
                            TxHistoryItemParam(
                                "isReward",
                                "true"
                            ),
                            TxHistoryItemParam(
                                "validator",
                                "validator_123"
                            )
                        )
                    )
                )
            )
            // Test Data End

            // Mocks Preparation Start
            coEvery {
                configDAO.historyUrl(
                    chainId = chainId
                )
            }.returns(requestUrl)

            coEvery {
                apolloClientStore.query(
                    serverUrl = requestUrl,
                    query = westendRequestToMock
                )
            }.returns(westendResponseToReturn)
            // Mocks Preparation End

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
            coVerify {
                apolloClientStore.query(
                    serverUrl = requestUrl,
                    query = westendRequestToMock
                )
            }.wasInvoked(1)

            assertTrue { result == expectedResult }
        }

    @Test
    fun `TEST westendHistoryInfoRemoteLoader_loadHistoryInfo EXPECT success FOR extrinsics AND extrinsic filter set`() =
        runTest {
            // Test Data Start
            val filters = setOf(TxFilter.EXTRINSIC)

            val westendRequestToMock =
                GetFearlessHistoryElementsQuery(
                    pageCount = pageCount,
                    address = signAddress,
                    cursor = cursor,
                    orderBy = Optional.present(listOf(HistoryElementsOrderBy.TIMESTAMP_DESC))
                )

            val westendResponseToReturn =
                GetFearlessHistoryElementsQuery.Data {
                    historyElements = buildHistoryElementsConnection {
                        nodes = listOf(
                            buildHistoryElement {
                                id = "id_123"
                                timestamp = "timestamp_123"
                                address = "address_someone"
                                reward = JsonObject(
                                    mapOf(
                                        "amount" to JsonPrimitive("amount_123"),
                                        "era" to JsonPrimitive("era_123"),
                                        "isReward" to JsonPrimitive("true"),
                                        "validator" to JsonPrimitive("validator_123")
                                    )
                                )
                                transfer = JsonObject(
                                    mapOf(
                                        "block" to JsonPrimitive("block_123"),
                                        "amount" to JsonPrimitive("amount_123"),
                                        "to" to JsonPrimitive("to_someone"),
                                        "from" to JsonPrimitive("from_someone"),
                                        "extrinsicHash" to JsonPrimitive("extrinsicHash_123"),
                                        "fee" to JsonPrimitive("fee_123"),
                                        "success" to JsonPrimitive("true")
                                    )
                                )
                                extrinsic = JsonObject(
                                    mapOf(
                                        "call" to JsonPrimitive("call_123"),
                                        "hash" to JsonPrimitive("hash_123"),
                                        "module" to JsonPrimitive("module_123"),
                                        "fee" to JsonPrimitive("fee_123"),
                                        "success" to JsonPrimitive("true")
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
                        blockHash = "",
                        module = "extrinsic",
                        method = "",
                        timestamp = "timestamp_123",
                        nestedData = null,
                        networkFee = "fee_123",
                        success = true,
                        data = listOfNotNull(
                            TxHistoryItemParam(
                                "call",
                                "call_123"
                            ),
                            TxHistoryItemParam(
                                "hash",
                                "hash_123"
                            ),
                            TxHistoryItemParam(
                                "module",
                                "module_123"
                            )
                        )
                    )
                )
            )
            // Test Data End

            // Mocks Preparation Start
            coEvery {
                configDAO.historyUrl(
                    chainId = chainId
                )
            }.returns(requestUrl)

            coEvery {
                apolloClientStore.query(
                    serverUrl = requestUrl,
                    query = westendRequestToMock
                )
            }.returns(westendResponseToReturn)
            // Mocks Preparation End

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
            coVerify {
                apolloClientStore.query(
                    serverUrl = requestUrl,
                    query = westendRequestToMock
                )
            }.wasInvoked(1)

            assertTrue { result == expectedResult }
        }

}