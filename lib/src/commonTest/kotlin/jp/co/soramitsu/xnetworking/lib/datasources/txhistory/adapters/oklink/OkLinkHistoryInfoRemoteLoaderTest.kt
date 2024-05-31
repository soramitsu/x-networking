package jp.co.soramitsu.xnetworking.lib.datasources.txhistory.adapters.oklink

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
import jp.co.soramitsu.xnetworking.lib.datasources.txhistory.impl.domain.adapters.oklink.OkLinkHistoryInfoRemoteLoader
import jp.co.soramitsu.xnetworking.lib.datasources.txhistory.impl.domain.adapters.oklink.OkLinkRequest
import jp.co.soramitsu.xnetworking.lib.datasources.txhistory.impl.domain.adapters.oklink.OkLinkResponse
import jp.co.soramitsu.xnetworking.lib.engines.rest.api.RestClient
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class OkLinkHistoryInfoRemoteLoaderTest {

    private companion object {
        const val chainId = "xLayer"
        const val requestUrl = "xLayer.url"

        const val contractAddress = ""
        const val symbol = "okb"

        const val cursor = ""
        const val pageCount = 1
        const val signAddress = ""

        val apiKeys = mapOf(chainId to "")
        val filters = emptySet<TxFilter>()
    }

    @Mock
    private val configDAO = mock(classOf<ConfigDAO>())

    @Mock
    private val restClient = mock(classOf<RestClient>())

    private val historyInfoRemoteLoader: HistoryInfoRemoteLoader =
        OkLinkHistoryInfoRemoteLoader(
            apiKeys = apiKeys,
            configDAO = configDAO,
            restClient = restClient
        )

    @Test
    fun `TEST okLinkHistoryInfoRemoteLoader_loadHistoryInfo EXPECT IllegalArgumentException BECAUSE chainInfo is not with asset symbol`() =
        runTest {
            // Test Data Start
            val okLinkRequestToMock =
                OkLinkRequest(
                    url = requestUrl,
                    address = signAddress,
                    apiKey = apiKeys[chainId]!!,
                    symbol = symbol
                )
            // Test Data End

            // Mocks Preparation Start
            coEvery {
                configDAO.historyUrl(
                    chainId = chainId
                )
            }.returns(requestUrl)
            // Mocks Preparation End

            assertFailsWith<IllegalArgumentException> {
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
                    request = okLinkRequestToMock
                )
            }.wasNotInvoked()
        }

    @Test
    fun `TEST okLinkHistoryInfoRemoteLoader_loadHistoryInfo EXPECT ExternalApiDAOException_NullUrl BECAUSE history url is null`() =
        runTest {
            // Test Data Start
            val ethereumType = "normal"

            val okLinkRequestToMock =
                OkLinkRequest(
                    url = requestUrl,
                    address = signAddress,
                    apiKey = apiKeys[chainId]!!,
                    symbol = symbol
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
                    chainInfo = ChainInfo.WithAssetSymbol(
                        chainId = chainId,
                        symbol = symbol
                    ),
                    filters = filters
                )
            }

            // Verification & Assertion
            coVerify {
                restClient.get(
                    request = okLinkRequestToMock
                )
            }.wasNotInvoked()
        }

    @Test
    fun `TEST okLinkHistoryInfoRemoteLoader_loadHistoryInfo EXPECT IllegalStateException BECAUSE responseCode != 0`() =
        runTest {
            // Test Data Start
            val okLinkRequestToMock =
                OkLinkRequest(
                    url = requestUrl,
                    address = signAddress,
                    apiKey = apiKeys[chainId]!!,
                    symbol = symbol
                )

            val okLinkResponseToReturn =
                OkLinkResponse(
                    code = 1,
                    msg = "",
                    data = emptyList()
                )
            // Test Data End

            // Mocks Preparation Start
            coEvery {
                configDAO.historyUrl(
                    chainId = chainId
                )
            }.returns(requestUrl)

            coEvery {
                restClient.get(
                    request = okLinkRequestToMock
                )
            }.returns(okLinkResponseToReturn)
            // Mocks Preparation End

            assertFailsWith<IllegalStateException> {
                historyInfoRemoteLoader.loadHistoryInfo(
                    pageCount = pageCount,
                    cursor = cursor,
                    signAddress = signAddress,
                    chainInfo = ChainInfo.WithAssetSymbol(
                        chainId = chainId,
                        symbol = symbol
                    ),
                    filters = filters
                )
            }

            // Verification & Assertion
            coVerify {
                restClient.get(
                    request = okLinkRequestToMock
                )
            }.wasInvoked(1)
        }

    @Test
    fun `TEST okLinkHistoryInfoRemoteLoader_loadHistoryInfo EXPECT emptyList BECAUSE data is null`() =
        runTest {
            // Test Data Start
            val okLinkRequestToMock =
                OkLinkRequest(
                    url = requestUrl,
                    address = signAddress,
                    apiKey = apiKeys[chainId]!!,
                    symbol = symbol
                )

            val okLinkResponseToReturn =
                OkLinkResponse(
                    code = 0,
                    msg = "",
                    data = emptyList()
                )

            val expectedResult = TxHistoryInfo(
                endCursor = null,
                endReached = true,
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
                restClient.get(
                    request = okLinkRequestToMock
                )
            }.returns(okLinkResponseToReturn)
            // Mocks Preparation End

            val result = historyInfoRemoteLoader.loadHistoryInfo(
                pageCount = pageCount,
                cursor = cursor,
                signAddress = signAddress,
                chainInfo = ChainInfo.WithAssetSymbol(
                    chainId = chainId,
                    symbol = symbol
                ),
                filters = filters
            )

            // Verification & Assertion
            coVerify {
                restClient.get(
                    request = okLinkRequestToMock
                )
            }.wasInvoked(1)

            assertTrue { result == expectedResult }
        }

    @Test
    fun `TEST okLinkHistoryInfoRemoteLoader_loadHistoryInfo EXPECT success`() =
        runTest {
            // Test Data Start
            val okLinkRequestToMock =
                OkLinkRequest(
                    url = requestUrl,
                    address = signAddress,
                    apiKey = apiKeys[chainId]!!,
                    symbol = symbol
                )

            val okLinkResponseToReturn =
                OkLinkResponse(
                    code = 0,
                    msg = "",
                    data = listOf(
                        OkLinkResponse.HistoryPage(
                            page = 1,
                            limit = pageCount,
                            totalPage = pageCount.toString(),
                            items = listOf(
                                OkLinkResponse.HistoryPage.HistoryItem(
                                    txId = "txId_123",
                                    methodId = "methodId_123",
                                    blockHash = "blockHash_123",
                                    height = "height_123",
                                    transactionTime = 123,
                                    from = "from_someone",
                                    to = "to_someone",
                                    isFromContract = true,
                                    isToContract = false,
                                    amount = "amount_123",
                                    transactionSymbol = symbol,
                                    txFee = "txFee_123",
                                    state = "success",
                                    tokenId = "tokenId_123",
                                    tokenContractAddress = contractAddress,
                                    challengeStatus = "challengeStatus_123",
                                    l1OriginHash = "originHash_123"
                                )
                            )
                        )
                    )
                )

            val expectedResult = TxHistoryInfo(
                endCursor = null,
                endReached = true,
                items = listOf(
                    TxHistoryItem(
                        id = "txId_123",
                        blockHash = "blockHash_123",
                        module = "",
                        method = "methodId_123",
                        timestamp = "123",
                        nestedData = emptyList(),
                        networkFee = "txFee_123",
                        success = true,
                        data = listOf(
                            TxHistoryItemParam(
                                "height",
                                "height_123"
                            ),
                            TxHistoryItemParam(
                                "tokenId",
                                "tokenId_123"
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
                                "isToContract",
                                "false"
                            ),
                            TxHistoryItemParam(
                                "from",
                                "from_someone"
                            ),
                            TxHistoryItemParam(
                                "isFromContract",
                                "true"
                            ),
                            TxHistoryItemParam(
                                "originHash",
                                "originHash_123"
                            ),
                            TxHistoryItemParam(
                                "transactionSymbol",
                                symbol
                            ),
                            TxHistoryItemParam(
                                "contractAddress",
                                contractAddress
                            ),
                            TxHistoryItemParam(
                                "challengeStatus",
                                "challengeStatus_123"
                            ),
                            TxHistoryItemParam(
                                "state",
                                "success"
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
                restClient.get(
                    request = okLinkRequestToMock
                )
            }.returns(okLinkResponseToReturn)
            // Mocks Preparation End

            val result = historyInfoRemoteLoader.loadHistoryInfo(
                pageCount = pageCount,
                cursor = cursor,
                signAddress = signAddress,
                chainInfo = ChainInfo.WithAssetSymbol(
                    chainId = chainId,
                    symbol = symbol
                ),
                filters = filters
            )

            // Verification & Assertion
            coVerify {
                restClient.get(
                    request = okLinkRequestToMock
                )
            }.wasInvoked(1)

            assertTrue { result == expectedResult }
        }
}