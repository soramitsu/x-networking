package jp.co.soramitsu.xnetworking.lib.datasources.txhistory.adapters.zeta

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
import jp.co.soramitsu.xnetworking.lib.datasources.txhistory.impl.domain.adapters.zeta.TokenTransfersZetaRequest
import jp.co.soramitsu.xnetworking.lib.datasources.txhistory.impl.domain.adapters.zeta.TransactionsZetaRequest
import jp.co.soramitsu.xnetworking.lib.datasources.txhistory.impl.domain.adapters.zeta.ZetaHistoryInfoRemoteLoader
import jp.co.soramitsu.xnetworking.lib.datasources.txhistory.impl.domain.adapters.zeta.ZetaResponse
import jp.co.soramitsu.xnetworking.lib.engines.rest.api.RestClient
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class ZetaHistoryInfoRemoteLoaderTest {

    private companion object {
        const val chainId = "zetachain"
        const val requestUrl = "zetachain.url"

        const val contractAddress = ""

        const val cursor = ""
        const val pageCount = 0
        const val signAddress = ""

        val filters = emptySet<TxFilter>()
    }

    @Mock
    private val configDAO = mock(classOf<ConfigDAO>())

    @Mock
    private val restClient = mock(classOf<RestClient>())

    private val historyInfoRemoteLoader: HistoryInfoRemoteLoader =
        ZetaHistoryInfoRemoteLoader(
            configDAO = configDAO,
            restClient = restClient
        )

    @Test
    fun `TEST zetaHistoryInfoRemoteLoader_loadHistoryInfo EXPECT IllegalArgumentException BECAUSE chainInfo is not with ethereumType`() =
        runTest {
            // Test Data Start
            val transactionsZetaRequestToMock =
                TransactionsZetaRequest(
                    url = requestUrl,
                    address = signAddress
                )

            val tokenTransfersZetaRequestToMock =
                TokenTransfersZetaRequest(
                    url = requestUrl,
                    address = signAddress,
                    assetId = contractAddress
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
                    request = transactionsZetaRequestToMock
                )
            }.wasNotInvoked()

            coVerify {
                restClient.get(
                    request = tokenTransfersZetaRequestToMock
                )
            }.wasNotInvoked()
        }

    @Test
    fun `TEST zetaHistoryInfoRemoteLoader_loadHistoryInfo EXPECT ExternalApiDAOException_NullUrl BECAUSE history url is null`() =
        runTest {
            // Test Data Start
            val ethereumType = "normal"

            val transactionsZetaRequestToMock =
                TransactionsZetaRequest(
                    url = requestUrl,
                    address = signAddress
                )

            val tokenTransfersZetaRequestToMock =
                TokenTransfersZetaRequest(
                    url = requestUrl,
                    address = signAddress,
                    assetId = contractAddress
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
                    chainInfo = ChainInfo.WithEthereumType(
                        chainId = chainId,
                        contractAddress = contractAddress,
                        ethereumType = ethereumType
                    ),
                    filters = filters
                )
            }

            // Verification & Assertion
            coVerify {
                restClient.get(
                    request = transactionsZetaRequestToMock
                )
            }.wasNotInvoked()

            coVerify {
                restClient.get(
                    request = tokenTransfersZetaRequestToMock
                )
            }.wasNotInvoked()
        }

    @Test
    fun `TEST zetaHistoryInfoRemoteLoader_loadHistoryInfo EXPECT success FOR normal ethereumType`() =
        runTest {
            // Test Data Start
            val ethereumType = "normal"

            val transactionsZetaRequestToMock =
                TransactionsZetaRequest(
                    url = requestUrl,
                    address = signAddress
                )

            val tokenTransfersZetaRequestToMock =
                TokenTransfersZetaRequest(
                    url = requestUrl,
                    address = signAddress,
                    assetId = contractAddress
                )

            val transactionsZetaResponseToReturn =
                ZetaResponse(
                    items = listOf(
                        ZetaResponse.HistoryItem(
                            timestamp = "timestamp_123",
                            fee = ZetaResponse.HistoryItem.Fee(
                                type = "",
                                value = "networkFee_123"
                            ),
                            status = "status",
                            to = ZetaResponse.HistoryItem.Address(
                                hash = "to_someone"
                            ),
                            from = ZetaResponse.HistoryItem.Address(
                                hash = "from_someone"
                            ),
                            value = "amount_123",
                            total = ZetaResponse.HistoryItem.Total(
                                value = "amount_123",
                                decimals = 0
                            ),
                            hash = "blockHash_123",
                            txHash = "txHash_123"
                        )
                    ),
                    nextPageParams = null
                )

            val expectedResult = TxHistoryInfo(
                endCursor = null,
                endReached = true,
                items = listOf(
                    TxHistoryItem(
                        id = "txHash_123",
                        blockHash = "blockHash_123",
                        module = "",
                        method = "",
                        timestamp = "timestamp_123",
                        nestedData = emptyList(),
                        networkFee = "networkFee_123",
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
                restClient.get(
                    request = transactionsZetaRequestToMock
                )
            }.returns(transactionsZetaResponseToReturn)
            // Mocks Preparation End

            val result = historyInfoRemoteLoader.loadHistoryInfo(
                pageCount = pageCount,
                cursor = cursor,
                signAddress = signAddress,
                chainInfo = ChainInfo.WithEthereumType(
                    chainId = chainId,
                    contractAddress = contractAddress,
                    ethereumType = ethereumType
                ),
                filters = filters
            )

            // Verification & Assertion
            coVerify {
                restClient.get(
                    request = transactionsZetaRequestToMock
                )
            }.wasInvoked(1)

            coVerify {
                restClient.get(
                    request = tokenTransfersZetaRequestToMock
                )
            }.wasNotInvoked()

            assertTrue { result == expectedResult }
        }

    @Test
    fun `TEST zetaHistoryInfoRemoteLoader_loadHistoryInfo EXPECT success FOR erc_bep ethereumType`() =
        runTest {
            // Test Data Start
            val ethereumType = "erc20"

            val transactionsZetaRequestToMock =
                TransactionsZetaRequest(
                    url = requestUrl,
                    address = signAddress
                )

            val tokenTransfersZetaRequestToMock =
                TokenTransfersZetaRequest(
                    url = requestUrl,
                    address = signAddress,
                    assetId = contractAddress
                )

            val tokenTransfersZetaResponseToReturn =
                ZetaResponse(
                    items = listOf(
                        ZetaResponse.HistoryItem(
                            timestamp = "timestamp_123",
                            fee = ZetaResponse.HistoryItem.Fee(
                                type = "",
                                value = "networkFee_123"
                            ),
                            status = "status",
                            to = ZetaResponse.HistoryItem.Address(
                                hash = "to_someone"
                            ),
                            from = ZetaResponse.HistoryItem.Address(
                                hash = "from_someone"
                            ),
                            value = "amount_123",
                            total = ZetaResponse.HistoryItem.Total(
                                value = "amount_123",
                                decimals = 0
                            ),
                            hash = "blockHash_123",
                            txHash = "txHash_123"
                        )
                    ),
                    nextPageParams = null
                )

            val expectedResult = TxHistoryInfo(
                endCursor = null,
                endReached = true,
                items = listOf(
                    TxHistoryItem(
                        id = "txHash_123",
                        blockHash = "blockHash_123",
                        module = "",
                        method = "",
                        timestamp = "timestamp_123",
                        nestedData = emptyList(),
                        networkFee = "networkFee_123",
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
                restClient.get(
                    request = tokenTransfersZetaRequestToMock
                )
            }.returns(tokenTransfersZetaResponseToReturn)
            // Mocks Preparation End

            val result = historyInfoRemoteLoader.loadHistoryInfo(
                pageCount = pageCount,
                cursor = cursor,
                signAddress = signAddress,
                chainInfo = ChainInfo.WithEthereumType(
                    chainId = chainId,
                    contractAddress = contractAddress,
                    ethereumType = ethereumType
                ),
                filters = filters
            )

            // Verification & Assertion
            coVerify {
                restClient.get(
                    request = transactionsZetaRequestToMock
                )
            }.wasNotInvoked()

            coVerify {
                restClient.get(
                    request = tokenTransfersZetaRequestToMock
                )
            }.wasInvoked(1)

            assertTrue { result == expectedResult }
        }

}