package jp.co.soramitsu.xnetworking.lib.datasources.txhistory.adapters.sorasubsquid

import io.mockative.Mock
import io.mockative.classOf
import io.mockative.coEvery
import io.mockative.coVerify
import io.mockative.eq
import io.mockative.mock
import jp.co.soramitsu.xnetworking.lib.datasources.chainsconfig.api.ConfigDAO
import jp.co.soramitsu.xnetworking.lib.datasources.chainsconfig.api.models.ExternalApiDAOException
import jp.co.soramitsu.xnetworking.lib.datasources.txhistory.api.models.ChainInfo
import jp.co.soramitsu.xnetworking.lib.datasources.txhistory.api.adapters.HistoryInfoRemoteLoader
import jp.co.soramitsu.xnetworking.lib.datasources.txhistory.api.models.TxFilter
import jp.co.soramitsu.xnetworking.lib.datasources.txhistory.api.models.TxHistoryInfo
import jp.co.soramitsu.xnetworking.lib.datasources.txhistory.api.models.TxHistoryItem
import jp.co.soramitsu.xnetworking.lib.datasources.txhistory.api.models.TxHistoryItemNested
import jp.co.soramitsu.xnetworking.lib.datasources.txhistory.api.models.TxHistoryItemParam
import jp.co.soramitsu.xnetworking.lib.datasources.txhistory.impl.domain.adapters.sorasubsquid.SoraSubSquidHistoryInfoRemoteLoader
import jp.co.soramitsu.xnetworking.lib.datasources.txhistory.impl.domain.adapters.sorasubsquid.SoraSubSquidRequest
import jp.co.soramitsu.xnetworking.lib.datasources.txhistory.impl.domain.adapters.sorasubsquid.SoraSubSquidResponse
import jp.co.soramitsu.xnetworking.lib.engines.rest.api.RestClient
import jp.co.soramitsu.xnetworking.lib.engines.utils.GraphQLResponseDataWrapper
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlin.test.Test
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class SoraSubSquidHistoryInfoRemoteLoaderTest {

    private companion object {
        const val chainId = "sora"
        const val requestUrl = "sora.url"

        const val cursor = ""
        const val pageCount = 1
        const val signAddress = ""
    }

    @Mock
    private val configDAO = mock(classOf<ConfigDAO>())

    @Mock
    private val restClient = mock(classOf<RestClient>())

    private val historyInfoRemoteLoader: HistoryInfoRemoteLoader =
        SoraSubSquidHistoryInfoRemoteLoader(
            configDAO = configDAO,
            restClient = restClient
        )

    @Test
    fun `TEST soraSubSquidHistoryInfoRemoteLoader_loadHistoryInfo EXPECT ExternalApiDAOException_NullUrl BECAUSE history url is null`() =
        runTest {
            // Test Data Start
            val filters = TxFilter.entries.toSet()

            val soraSubSquidRequestToMock =
                SoraSubSquidRequest(
                    url = requestUrl,
                    address = signAddress,
                    limit = pageCount,
                    cursor = cursor
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
                restClient.post(
                    request = eq(soraSubSquidRequestToMock)
                )
            }.wasNotInvoked()
        }

    @Test
    fun `TEST soraSubSquidHistoryInfoRemoteLoader_loadHistoryInfo EXPECT emptyList BECAUSE filter set is empty`() =
        runTest {
            // Test Data Start
            val filters = emptySet<TxFilter>()

            val soraSubSquidRequestToMock =
                SoraSubSquidRequest(
                    url = requestUrl,
                    address = signAddress,
                    limit = pageCount,
                    cursor = cursor
                )

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
                restClient.post(
                    request = eq(soraSubSquidRequestToMock)
                )
            }.wasNotInvoked()

            assertTrue { result == expectedResult }
        }

    @Test
    fun `TEST soraSubSquidHistoryInfoRemoteLoader_loadHistoryInfo EXPECT success`() =
        runTest {
            // Test Data Start
            val filters = TxFilter.entries.toSet()

            val soraSubSquidRequestToMock =
                SoraSubSquidRequest(
                    url = requestUrl,
                    address = signAddress,
                    limit = pageCount,
                    cursor = cursor
                )

            val soraSubSquidResponseToReturn =
                GraphQLResponseDataWrapper(
                    data = SoraSubSquidResponse(
                        historyElementsConnection = SoraSubSquidResponse.HistoryElementsConnection(
                            edges = listOf(
                                SoraSubSquidResponse.HistoryElementsConnection.Edge(
                                    node = SoraSubSquidResponse.HistoryElementsConnection.Edge.Node(
                                        id = "id_123",
                                        timestamp = "timestamp_123",
                                        networkFee = "fee_123",
                                        module = "module_123",
                                        method = "method_123",
                                        execution = SoraSubSquidResponse.HistoryElementsConnection.Edge.Node.ExecutionResult(
                                            success = true,
                                            error = null
                                        ),
                                        address = "address_someone",
                                        blockHash = "blockHash_123",
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
                                    )
                                )
                            ),
                            pageInfo = SoraSubSquidResponse.HistoryElementsConnection.PageInfo(
                                endCursor = "endCursor_123",
                                hasNextPage = false
                            )
                        )
                    )
                )

            val expectedResult = TxHistoryInfo(
                endCursor = "endCursor_123",
                endReached = true,
                items = listOf(
                    TxHistoryItem(
                        id = "id_123",
                        blockHash = "blockHash_123",
                        module = "module_123",
                        method = "method_123",
                        timestamp = "timestamp_123",
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
            // Test Data End

            // Mocks Preparation Start
            coEvery {
                configDAO.historyUrl(
                    chainId = chainId
                )
            }.returns(requestUrl)

            coEvery {
                restClient.post(
                    request = eq(soraSubSquidRequestToMock)
                )
            }.returns(soraSubSquidResponseToReturn)
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
                restClient.post(
                    request = eq(soraSubSquidRequestToMock)
                )
            }.wasInvoked(1)

            assertTrue { result == expectedResult }
        }
}