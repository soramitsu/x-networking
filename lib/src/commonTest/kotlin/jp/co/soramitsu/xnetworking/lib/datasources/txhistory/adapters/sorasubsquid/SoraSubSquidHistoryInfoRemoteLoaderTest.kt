package jp.co.soramitsu.xnetworking.lib.datasources.txhistory.adapters.sorasubsquid

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
import jp.co.soramitsu.xnetworking.lib.datasources.txhistory.impl.domain.adapters.sorasubsquid.SoraSubSquidHistoryInfoRemoteLoader
import jp.co.soramitsu.xnetworking.lib.datasources.txhistory.impl.domain.adapters.sorasubsquid.SoraSubSquidRequest
import jp.co.soramitsu.xnetworking.lib.datasources.txhistory.impl.domain.adapters.sorasubsquid.SoraSubSquidResponse
import jp.co.soramitsu.xnetworking.lib.engines.rest.api.RestClient
import jp.co.soramitsu.xnetworking.lib.engines.rest.api.models.AbstractRestServerRequest
import jp.co.soramitsu.xnetworking.lib.engines.utils.GraphQLResponseDataWrapper
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlin.test.Test
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

private class FakeConfigDao(
    private val historyUrl: String?,
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

private open class FakeRestClient(
    private val response: GraphQLResponseDataWrapper<SoraSubSquidResponse>? = null
) : RestClient() {
    override suspend fun <T> post(request: AbstractRestServerRequest.WithBody<T>): T {
        return response as T
    }

    override suspend fun postAsString(request: AbstractRestServerRequest.WithBody<String>): String {
        TODO("Not yet implemented")
    }

    override suspend fun <T> get(request: AbstractRestServerRequest<T>): T {
        TODO("Not yet implemented")
    }

    override suspend fun getAsString(request: AbstractRestServerRequest<String>): String {
        TODO("Not yet implemented")
    }
}

class SoraSubSquidHistoryInfoRemoteLoaderTest {

    private companion object {
        const val chainId = "sora"
        const val requestUrl = "sora.url"

        const val cursor = ""
        const val pageCount = 1
        const val signAddress = ""
    }


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
            val historyInfoRemoteLoader: HistoryInfoRemoteLoader =
                SoraSubSquidHistoryInfoRemoteLoader(
                    configDAO = FakeConfigDao(null),
                    restClient = FakeRestClient()
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
//                restClient.post(
//                    request = eq(soraSubSquidRequestToMock)
//                )
//            }.wasNotInvoked()
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

            val historyInfoRemoteLoader: HistoryInfoRemoteLoader =
                SoraSubSquidHistoryInfoRemoteLoader(
                    configDAO = FakeConfigDao(requestUrl),
                    restClient = FakeRestClient()
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
//                restClient.post(
//                    request = eq(soraSubSquidRequestToMock)
//                )
//            }.wasNotInvoked()

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

            val historyInfoRemoteLoader: HistoryInfoRemoteLoader =
                SoraSubSquidHistoryInfoRemoteLoader(
                    configDAO = FakeConfigDao(requestUrl),
                    restClient = FakeRestClient(soraSubSquidResponseToReturn)
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
//                restClient.post(
//                    request = eq(soraSubSquidRequestToMock)
//                )
//            }.wasInvoked(1)

            assertTrue { result == expectedResult }
        }
}