package jp.co.soramitsu.xnetworking.lib.datasources.txhistory.adapters.oklink

import jp.co.soramitsu.xnetworking.lib.datasources.chainsconfig.api.ConfigDAO
import jp.co.soramitsu.xnetworking.lib.datasources.chainsconfig.api.models.ExternalApiDAOException
import jp.co.soramitsu.xnetworking.lib.datasources.chainsconfig.api.models.ExternalApiType
import jp.co.soramitsu.xnetworking.lib.datasources.chainsconfig.api.models.StakingOption
import jp.co.soramitsu.xnetworking.lib.datasources.txhistory.api.adapters.HistoryInfoRemoteLoader
import jp.co.soramitsu.xnetworking.lib.datasources.txhistory.api.models.ChainInfo
import jp.co.soramitsu.xnetworking.lib.datasources.txhistory.api.models.TxFilter
import jp.co.soramitsu.xnetworking.lib.datasources.txhistory.api.models.TxHistoryInfo
import jp.co.soramitsu.xnetworking.lib.datasources.txhistory.api.models.TxHistoryItem
import jp.co.soramitsu.xnetworking.lib.datasources.txhistory.api.models.TxHistoryItemParam
import jp.co.soramitsu.xnetworking.lib.datasources.txhistory.impl.domain.adapters.oklink.OkLinkHistoryInfoRemoteLoader
import jp.co.soramitsu.xnetworking.lib.datasources.txhistory.impl.domain.adapters.oklink.OkLinkRequest
import jp.co.soramitsu.xnetworking.lib.datasources.txhistory.impl.domain.adapters.oklink.OkLinkResponse
import jp.co.soramitsu.xnetworking.lib.engines.rest.api.RestClient
import jp.co.soramitsu.xnetworking.lib.engines.rest.api.models.AbstractRestServerRequest
import kotlinx.coroutines.test.runTest
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
    private val response: OkLinkResponse? = null
) : RestClient() {
    override suspend fun <T> post(request: AbstractRestServerRequest.WithBody<T>): T {
        TODO("Not yet implemented")
    }

    override suspend fun postAsString(request: AbstractRestServerRequest.WithBody<String>): String {
        TODO("Not yet implemented")
    }

    override suspend fun <T> get(request: AbstractRestServerRequest<T>): T {
        return response as T
    }

    override suspend fun getAsString(request: AbstractRestServerRequest<String>): String {
        TODO("Not yet implemented")
    }
}

class OkLinkHistoryInfoRemoteLoaderTest {

    private companion object {
        const val chainId = "xLayer"
        const val requestUrl = "xLayer.url"

        const val contractAddress = ""
        const val symbol = "okb"

        const val cursor = ""
        const val pageCount = 1
        const val signAddress = ""

        val filters = emptySet<TxFilter>()
    }

    @Test
    fun `TEST okLinkHistoryInfoRemoteLoader_loadHistoryInfo EXPECT IllegalArgumentException BECAUSE chainInfo is not with asset symbol`() =
        runTest {
            // Test Data Start
            val okLinkRequestToMock =
                OkLinkRequest(
                    url = requestUrl,
                    address = signAddress,
                    apiKey = "",
                    symbol = symbol
                )

            val historyInfoRemoteLoader: HistoryInfoRemoteLoader =
                OkLinkHistoryInfoRemoteLoader(
                    configDAO = FakeConfigDao(requestUrl),
                    restClient = FakeRestClient()
                )
            // Test Data End

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
//            coVerify {
//                restClient.get(
//                    request = okLinkRequestToMock
//                )
//            }.wasNotInvoked()
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
                    apiKey = "",
                    symbol = symbol
                )

            val historyInfoRemoteLoader: HistoryInfoRemoteLoader =
                OkLinkHistoryInfoRemoteLoader(
                    configDAO = FakeConfigDao(null),
                    restClient = FakeRestClient()
                )
            // Test Data End

            assertFailsWith<ExternalApiDAOException.NullUrl> {
                historyInfoRemoteLoader.loadHistoryInfo(
                    pageCount = pageCount,
                    cursor = cursor,
                    signAddress = signAddress,
                    chainInfo = ChainInfo.OkLink(
                        chainId = chainId,
                        symbol = symbol,
                        apiKey = ""
                    ),
                    filters = filters
                )
            }

            // Verification & Assertion
//            coVerify {
//                restClient.get(
//                    request = okLinkRequestToMock
//                )
//            }.wasNotInvoked()
        }

    @Test
    fun `TEST okLinkHistoryInfoRemoteLoader_loadHistoryInfo EXPECT IllegalStateException BECAUSE responseCode != 0`() =
        runTest {
            // Test Data Start
            val okLinkRequestToMock =
                OkLinkRequest(
                    url = requestUrl,
                    address = signAddress,
                    apiKey = "",
                    symbol = symbol
                )

            val okLinkResponseToReturn =
                OkLinkResponse(
                    code = 1,
                    msg = "",
                    data = emptyList()
                )

            val historyInfoRemoteLoader: HistoryInfoRemoteLoader =
                OkLinkHistoryInfoRemoteLoader(
                    configDAO = FakeConfigDao(requestUrl),
                    restClient = FakeRestClient(okLinkResponseToReturn)
                )
            // Test Data End

            assertFailsWith<IllegalStateException> {
                historyInfoRemoteLoader.loadHistoryInfo(
                    pageCount = pageCount,
                    cursor = cursor,
                    signAddress = signAddress,
                    chainInfo = ChainInfo.OkLink(
                        chainId = chainId,
                        symbol = symbol,
                        apiKey = ""
                    ),
                    filters = filters
                )
            }

            // Verification & Assertion
//            coVerify {
//                restClient.get(
//                    request = okLinkRequestToMock
//                )
//            }.wasInvoked(1)
        }

    @Test
    fun `TEST okLinkHistoryInfoRemoteLoader_loadHistoryInfo EXPECT emptyList BECAUSE data is null`() =
        runTest {
            // Test Data Start
            val okLinkRequestToMock =
                OkLinkRequest(
                    url = requestUrl,
                    address = signAddress,
                    apiKey = "",
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

            val historyInfoRemoteLoader: HistoryInfoRemoteLoader =
                OkLinkHistoryInfoRemoteLoader(
                    configDAO = FakeConfigDao(requestUrl),
                    restClient = FakeRestClient(okLinkResponseToReturn)
                )
            // Test Data End

            val result = historyInfoRemoteLoader.loadHistoryInfo(
                pageCount = pageCount,
                cursor = cursor,
                signAddress = signAddress,
                chainInfo = ChainInfo.OkLink(
                    chainId = chainId,
                    symbol = symbol,
                    apiKey = ""
                ),
                filters = filters
            )

            // Verification & Assertion
//            coVerify {
//                restClient.get(
//                    request = okLinkRequestToMock
//                )
//            }.wasInvoked(1)

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
                    apiKey = "",
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

            val historyInfoRemoteLoader: HistoryInfoRemoteLoader =
                OkLinkHistoryInfoRemoteLoader(
                    configDAO = FakeConfigDao(requestUrl),
                    restClient = FakeRestClient(okLinkResponseToReturn)
                )
            // Test Data End

            val result = historyInfoRemoteLoader.loadHistoryInfo(
                pageCount = pageCount,
                cursor = cursor,
                signAddress = signAddress,
                chainInfo = ChainInfo.OkLink(
                    chainId = chainId,
                    symbol = symbol,
                    apiKey = ""
                ),
                filters = filters
            )

            // Verification & Assertion
//            coVerify {
//                restClient.get(
//                    request = okLinkRequestToMock
//                )
//            }.wasInvoked(1)

            assertTrue { result == expectedResult }
        }
}