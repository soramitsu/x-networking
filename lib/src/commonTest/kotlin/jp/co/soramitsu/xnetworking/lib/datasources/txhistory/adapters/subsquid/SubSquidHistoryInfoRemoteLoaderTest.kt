package jp.co.soramitsu.xnetworking.lib.datasources.txhistory.adapters.subsquid

import io.mockative.Mock
import io.mockative.classOf
import io.mockative.coEvery
import io.mockative.coVerify
import io.mockative.mock
import jp.co.soramitsu.xnetworking.lib.datasources.chainsconfig.api.ConfigDAO
import jp.co.soramitsu.xnetworking.lib.datasources.chainsconfig.api.models.ExternalApiDAOException
import jp.co.soramitsu.xnetworking.lib.datasources.txhistory.api.models.ChainInfo
import jp.co.soramitsu.xnetworking.lib.datasources.txhistory.api.adapters.HistoryInfoRemoteLoader
import jp.co.soramitsu.xnetworking.lib.datasources.txhistory.api.models.TxFilter
import jp.co.soramitsu.xnetworking.lib.datasources.txhistory.api.models.TxHistoryInfo
import jp.co.soramitsu.xnetworking.lib.datasources.txhistory.api.models.TxHistoryItem
import jp.co.soramitsu.xnetworking.lib.datasources.txhistory.api.models.TxHistoryItemParam
import jp.co.soramitsu.xnetworking.lib.engines.utils.GraphQLResponseDataWrapper
import jp.co.soramitsu.xnetworking.lib.datasources.txhistory.impl.domain.adapters.subsquid.SubSquidHistoryInfoRemoteLoader
import jp.co.soramitsu.xnetworking.lib.datasources.txhistory.impl.domain.adapters.subsquid.SubSquidRequest
import jp.co.soramitsu.xnetworking.lib.datasources.txhistory.impl.domain.adapters.subsquid.SubSquidResponse
import jp.co.soramitsu.xnetworking.lib.engines.rest.api.RestClient
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class SubSquidHistoryInfoRemoteLoaderTest {

    private companion object {
        const val chainId = "polkadot"
        const val requestUrl = "polkadot.url"

        const val cursor = ""
        const val pageCount = 0
        const val signAddress = ""
    }

    @Mock
    private val configDAO = mock(classOf<ConfigDAO>())

    @Mock
    private val restClient = mock(classOf<RestClient>())

    private val historyInfoRemoteLoader: HistoryInfoRemoteLoader =
        SubSquidHistoryInfoRemoteLoader(
            configDAO = configDAO,
            restClient = restClient
        )

    @Test
    fun `TEST subSquidHistoryInfoRemoteLoader_loadHistoryInfo EXPECT ExternalApiDAOException_NullUrl BECAUSE history url is null`() =
        runTest {
            // Test Data Start
            val filters = TxFilter.entries.toSet()

            val subSquidRequestToMock =
                SubSquidRequest(
                    url = requestUrl,
                    address = signAddress,
                    cursor = cursor,
                    limit = pageCount
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
                restClient.get(
                    request = subSquidRequestToMock
                )
            }.wasNotInvoked()
        }

    @Test
    fun `TEST subSquidHistoryInfoRemoteLoader_loadHistoryInfo EXPECT emptyList FOR empty filters set`() =
        runTest {
            // Test Data Start
            val filters = emptySet<TxFilter>()

            val subSquidRequestToMock =
                SubSquidRequest(
                    url = requestUrl,
                    address = signAddress,
                    cursor = cursor,
                    limit = pageCount
                )

            val subSquidResponseToReturn =
                GraphQLResponseDataWrapper(
                    data = SubSquidResponse(
                        historyElementsConnection = SubSquidResponse.HistoryElementsConnection(
                            pageInfo = SubSquidResponse.HistoryElementsConnection.PageInfo(
                                hasNextPage = false,
                                endCursor = "endCursor_123"
                            ),
                            edges = listOf(
                                SubSquidResponse.HistoryElementsConnection.Node(
                                    node = SubSquidResponse.HistoryElementsConnection.Node.HistoryElement(
                                        timestamp = 123,
                                        id = "id_123",
                                        extrinsicIdx = "extrinsicIdx_123",
                                        extrinsicHash = "extrinsicHash_123",
                                        address = "",
                                        success = true,
                                        transfer = SubSquidResponse.HistoryElementsConnection.Node.HistoryElement.Transfer(
                                            amount = "amount_123",
                                            fee = "123",
                                            from = "from_someone",
                                            to = "to_someone"
                                        ),
                                        reward = null
                                    )
                                )
                            )
                        )
                    )
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

            coEvery {
                restClient.post(
                    request = subSquidRequestToMock
                )
            }.returns(subSquidResponseToReturn)
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
                    request = subSquidRequestToMock
                )
            }.wasNotInvoked()

            assertTrue { result == expectedResult }
        }

    @Test
    fun `TEST subSquidHistoryInfoRemoteLoader_loadHistoryInfo EXPECT success FOR transfers AND transfer filter`() =
        runTest {
            // Test Data Start
            val filters = setOf(TxFilter.TRANSFER)

            val subSquidRequestToMock =
                SubSquidRequest(
                    url = requestUrl,
                    address = signAddress,
                    cursor = cursor,
                    limit = pageCount
                )

            val subSquidResponseToReturn =
                GraphQLResponseDataWrapper(
                    data = SubSquidResponse(
                        historyElementsConnection = SubSquidResponse.HistoryElementsConnection(
                            pageInfo = SubSquidResponse.HistoryElementsConnection.PageInfo(
                                hasNextPage = false,
                                endCursor = "endCursor_123"
                            ),
                            edges = listOf(
                                SubSquidResponse.HistoryElementsConnection.Node(
                                    node = SubSquidResponse.HistoryElementsConnection.Node.HistoryElement(
                                        timestamp = 123,
                                        id = "id_123",
                                        extrinsicIdx = "extrinsicIdx_123",
                                        extrinsicHash = "extrinsicHash_123",
                                        address = "",
                                        success = true,
                                        transfer = SubSquidResponse.HistoryElementsConnection.Node.HistoryElement.Transfer(
                                            amount = "amount_123",
                                            fee = "123",
                                            from = "from_someone",
                                            to = "to_someone"
                                        ),
                                        reward = null
                                    )
                                )
                            )
                        )
                    )
                )

            val expectedResult = TxHistoryInfo(
                endCursor = "endCursor_123",
                endReached = true,
                items = listOf(
                    TxHistoryItem(
                        id = "extrinsicIdx_123",
                        blockHash = "extrinsicHash_123",
                        module = "",
                        method = "",
                        timestamp = "123",
                        nestedData = emptyList(),
                        networkFee = "123",
                        success = true,
                        data = listOf(
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
                restClient.post(
                    request = subSquidRequestToMock
                )
            }.returns(subSquidResponseToReturn)
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
                    request = subSquidRequestToMock
                )
            }.wasInvoked(1)

            assertTrue { result == expectedResult }
        }

    @Test
    fun `TEST subSquidHistoryInfoRemoteLoader_loadHistoryInfo EXPECT success FOR rewards AND reward filter`() =
        runTest {
            // Test Data Start
            val filters = setOf(TxFilter.REWARD)

            val subSquidRequestToMock =
                SubSquidRequest(
                    url = requestUrl,
                    address = signAddress,
                    cursor = cursor,
                    limit = pageCount
                )

            val subSquidResponseToReturn =
                GraphQLResponseDataWrapper(
                    data = SubSquidResponse(
                        historyElementsConnection = SubSquidResponse.HistoryElementsConnection(
                            pageInfo = SubSquidResponse.HistoryElementsConnection.PageInfo(
                                hasNextPage = false,
                                endCursor = "endCursor_123"
                            ),
                            edges = listOf(
                                SubSquidResponse.HistoryElementsConnection.Node(
                                    node = SubSquidResponse.HistoryElementsConnection.Node.HistoryElement(
                                        timestamp = 123,
                                        id = "id_123",
                                        extrinsicIdx = "extrinsicIdx_123",
                                        extrinsicHash = "extrinsicHash_123",
                                        address = "",
                                        success = true,
                                        transfer = null,
                                        reward = SubSquidResponse.HistoryElementsConnection.Node.HistoryElement.Reward(
                                            amount = "amount_123",
                                            era = 123,
                                            stash = "stash_123"
                                        )
                                    )
                                )
                            )
                        )
                    )
                )

            val expectedResult = TxHistoryInfo(
                endCursor = "endCursor_123",
                endReached = true,
                items = listOf(
                    TxHistoryItem(
                        id = "extrinsicIdx_123",
                        blockHash = "extrinsicHash_123",
                        module = "",
                        method = "",
                        timestamp = "123",
                        nestedData = emptyList(),
                        networkFee = "0",
                        success = true,
                        data = listOf(
                            TxHistoryItemParam(
                                "amount",
                                "amount_123"
                            ),
                            TxHistoryItemParam(
                                "era",
                                "123"
                            ),
                            TxHistoryItemParam(
                                "stash",
                                "stash_123"
                            ),
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
                restClient.post(
                    request = subSquidRequestToMock
                )
            }.returns(subSquidResponseToReturn)
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
                    request = subSquidRequestToMock
                )
            }.wasInvoked(1)

            assertTrue { result == expectedResult }
        }

}