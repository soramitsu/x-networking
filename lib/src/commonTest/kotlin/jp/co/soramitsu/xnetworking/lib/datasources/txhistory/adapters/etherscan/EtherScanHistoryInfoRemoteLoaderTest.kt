package jp.co.soramitsu.xnetworking.lib.datasources.txhistory.adapters.etherscan

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
import jp.co.soramitsu.xnetworking.lib.datasources.txhistory.impl.domain.adapters.etherscan.ErcBepEtherScanRequest
import jp.co.soramitsu.xnetworking.lib.datasources.txhistory.impl.domain.adapters.etherscan.EtherScanHistoryInfoRemoteLoader
import jp.co.soramitsu.xnetworking.lib.datasources.txhistory.impl.domain.adapters.etherscan.EtherScanResponse
import jp.co.soramitsu.xnetworking.lib.datasources.txhistory.impl.domain.adapters.etherscan.NormalEtherScanRequest
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
        return historyUrl ?: throw ExternalApiDAOException.NullUrl(chainId)    }

    override suspend fun stakingType(chainId: String): ExternalApiType {
        TODO("Not yet implemented")
    }

    override suspend fun stakingUrl(chainId: String): String {
        TODO("Not yet implemented")
    }

    override suspend fun staking(chainId: String): StakingOption? {
        TODO("Not yet implemented")    }
}

private open class FakeRestClient(
    private val response: EtherScanResponse? = null
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

class EtherScanHistoryInfoRemoteLoaderTest {

    private companion object {
        const val chainId = "ethereum"
        const val requestUrl = "ethereum.url"

        const val contractAddress = ""

        const val cursor = ""
        const val pageCount = 0
        const val signAddress = ""

        val filters = emptySet<TxFilter>()
    }

    @Test
    fun `TEST etherScanHistoryInfoRemoteLoader_loadHistoryInfo EXPECT IllegalArgumentException BECAUSE chainInfo is not with ethereumType`() =
        runTest {
            // Test Data Start
//            val etherScanNormalRequestToMock =
//                NormalEtherScanRequest(
//                    url = requestUrl,
//                    address = signAddress,
//                    apiKey = ""
//                )
//
//            val etherScanErcBepRequestToMock =
//                ErcBepEtherScanRequest(
//                    url = requestUrl,
//                    address = signAddress,
//                    apiKey = "",
//                    contractAddress = contractAddress
//                )
            val historyInfoRemoteLoader: HistoryInfoRemoteLoader =
                EtherScanHistoryInfoRemoteLoader(
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
//                    request = etherScanNormalRequestToMock
//                )
//            }.wasNotInvoked()
//
//            coVerify {
//                restClient.get(
//                    request = etherScanErcBepRequestToMock
//                )
//            }.wasNotInvoked()
        }

    @Test
    fun `TEST etherScanHistoryInfoRemoteLoader_loadHistoryInfo EXPECT ExternalApiDAOException_NullUrl BECAUSE history url is null`() =
        runTest {
            // Test Data Start
            val ethereumType = "normal"

//            val etherScanNormalRequestToMock =
//                NormalEtherScanRequest(
//                    url = requestUrl,
//                    address = signAddress,
//                    apiKey = ""
//                )
//
//            val etherScanErcBepRequestToMock =
//                ErcBepEtherScanRequest(
//                    url = requestUrl,
//                    address = signAddress,
//                    apiKey = "",
//                    contractAddress = contractAddress
//                )

            val historyInfoRemoteLoader: HistoryInfoRemoteLoader =
                EtherScanHistoryInfoRemoteLoader(
                    configDAO = FakeConfigDao(null),
                    restClient = FakeRestClient()
                )
            // Test Data End

            assertFailsWith<ExternalApiDAOException.NullUrl> {
                historyInfoRemoteLoader.loadHistoryInfo(
                    pageCount = pageCount,
                    cursor = cursor,
                    signAddress = signAddress,
                    chainInfo = ChainInfo.Ethereum(
                        chainId = chainId,
                        contractAddress = contractAddress,
                        ethereumType = ethereumType,
                        apiKey = ""
                    ),
                    filters = filters
                )
            }

            // Verification & Assertion
//            coVerify {
//                restClient.get(
//                    request = etherScanNormalRequestToMock
//                )
//            }.wasNotInvoked()
//
//            coVerify {
//                restClient.get(
//                    request = etherScanErcBepRequestToMock
//                )
//            }.wasNotInvoked()
        }

    @Test
    fun `TEST etherScanHistoryInfoRemoteLoader_loadHistoryInfo EXPECT IllegalStateException BECAUSE ethereumType is null`() =
        runTest {
            // Test Data Start
            val ethereumType = null

//            val etherScanNormalRequestToMock =
//                NormalEtherScanRequest(
//                    url = requestUrl,
//                    address = signAddress,
//                    apiKey = ""
//                )
//
//            val etherScanErcBepRequestToMock =
//                ErcBepEtherScanRequest(
//                    url = requestUrl,
//                    address = signAddress,
//                    apiKey = "",
//                    contractAddress = contractAddress
//                )
            val historyInfoRemoteLoader: HistoryInfoRemoteLoader =
                EtherScanHistoryInfoRemoteLoader(
                    configDAO = FakeConfigDao(requestUrl),
                    restClient = FakeRestClient()
                )
            // Test Data End

            assertFailsWith<IllegalStateException> {
                historyInfoRemoteLoader.loadHistoryInfo(
                    pageCount = pageCount,
                    cursor = cursor,
                    signAddress = signAddress,
                    chainInfo = ChainInfo.Ethereum(
                        chainId = chainId,
                        contractAddress = contractAddress,
                        ethereumType = ethereumType,
                        apiKey = ""
                    ),
                    filters = filters
                )
            }

            // Verification & Assertion
//            coVerify {
//                restClient.get(
//                    request = etherScanNormalRequestToMock
//                )
//            }.wasNotInvoked()
//
//            coVerify {
//                restClient.get(
//                    request = etherScanErcBepRequestToMock
//                )
//            }.wasNotInvoked()
        }

    @Test
    fun `TEST etherScanHistoryInfoRemoteLoader_loadHistoryInfo EXPECT success FOR normal ethereumType`() =
        runTest {
            // Test Data Start
            val ethereumType = "normal"

            val etherScanNormalRequestToMock =
                NormalEtherScanRequest(
                    url = requestUrl,
                    address = signAddress,
                    apiKey = ""
                )

            val etherScanErcBepRequestToMock =
                ErcBepEtherScanRequest(
                    url = requestUrl,
                    address = signAddress,
                    apiKey = "",
                    contractAddress = contractAddress
                )

            val etherScanNormalResponseToReturn =
                EtherScanResponse(
                    status = 1,
                    message = "",
                    result = listOf(
                        EtherScanResponse.HistoryElement(
                            blockNumber = "blockNumber_123",
                            timeStamp = 123,
                            hash = "hash_123",
                            nonce = "nonce_123",
                            blockHash = "blockHash_123",
                            from = "from_someone",
                            to = "to_someone",
                            contractAddress = contractAddress,
                            value = "amount_123",
                            gas = "gas_123",
                            gasPrice = "gasPrice_123",
                            gasUsed = "gasUsed_123",
                            isError = 0
                        )
                    )
                )

            val expectedResult = TxHistoryInfo(
                endCursor = null,
                endReached = true,
                items = listOf(
                    TxHistoryItem(
                        id = "hash_123",
                        blockHash = "blockHash_123",
                        module = "",
                        method = "",
                        timestamp = "123",
                        nestedData = emptyList(),
                        networkFee = "0",
                        success = true,
                        data = listOf(
                            TxHistoryItemParam(
                                "blockNumber",
                                "blockNumber_123"
                            ),
                            TxHistoryItemParam(
                                "nonce",
                                "nonce_123"
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
                                "gas",
                                "gas_123"
                            ),
                            TxHistoryItemParam(
                                "gasPrice",
                                "gasPrice_123"
                            ),
                            TxHistoryItemParam(
                                "gasUsed",
                                "gasUsed_123"
                            ),
                            TxHistoryItemParam(
                                "contractAddress",
                                contractAddress
                            )
                        )
                    )
                )
            )
            val historyInfoRemoteLoader: HistoryInfoRemoteLoader =
                EtherScanHistoryInfoRemoteLoader(
                    configDAO = FakeConfigDao(requestUrl),
                    restClient = FakeRestClient(etherScanNormalResponseToReturn)
                )
            // Test Data End

            val result = historyInfoRemoteLoader.loadHistoryInfo(
                pageCount = pageCount,
                cursor = cursor,
                signAddress = signAddress,
                chainInfo = ChainInfo.Ethereum(
                    chainId = chainId,
                    contractAddress = contractAddress,
                    ethereumType = ethereumType,
                    apiKey = ""
                ),
                filters = filters
            )

            // Verification & Assertion
//            coVerify {
//                restClient.get(
//                    request = etherScanNormalRequestToMock
//                )
//            }.wasInvoked(1)
//
//            coVerify {
//                restClient.get(
//                    request = etherScanErcBepRequestToMock
//                )
//            }.wasNotInvoked()

            assertTrue { result == expectedResult }
        }

    @Test
    fun `TEST etherScanHistoryInfoRemoteLoader_loadHistoryInfo EXPECT success FOR erc_bep ethereumType`() =
        runTest {
            // Test Data Start
            val ethereumType = "erc20"

            val etherScanNormalRequestToMock =
                NormalEtherScanRequest(
                    url = requestUrl,
                    address = signAddress,
                    apiKey = ""
                )

            val etherScanErcBepRequestToMock =
                ErcBepEtherScanRequest(
                    url = requestUrl,
                    address = signAddress,
                    apiKey = "",
                    contractAddress = contractAddress
                )

            val etherScanErcBepResponseToReturn =
                EtherScanResponse(
                    status = 1,
                    message = "",
                    result = listOf(
                        EtherScanResponse.HistoryElement(
                            blockNumber = "blockNumber_123",
                            timeStamp = 123,
                            hash = "hash_123",
                            nonce = "nonce_123",
                            blockHash = "blockHash_123",
                            from = "from_someone",
                            to = "to_someone",
                            contractAddress = contractAddress,
                            value = "amount_123",
                            gas = "gas_123",
                            gasPrice = "gasPrice_123",
                            gasUsed = "gasUsed_123",
                            isError = 0
                        )
                    )
                )

            val historyInfoRemoteLoader: HistoryInfoRemoteLoader =
                EtherScanHistoryInfoRemoteLoader(
                    configDAO = FakeConfigDao(requestUrl),
                    restClient = FakeRestClient(etherScanErcBepResponseToReturn)
                )

            val expectedResult = TxHistoryInfo(
                endCursor = null,
                endReached = true,
                items = listOf(
                    TxHistoryItem(
                        id = "hash_123",
                        blockHash = "blockHash_123",
                        module = "",
                        method = "",
                        timestamp = "123",
                        nestedData = emptyList(),
                        networkFee = "0",
                        success = true,
                        data = listOf(
                            TxHistoryItemParam(
                                "blockNumber",
                                "blockNumber_123"
                            ),
                            TxHistoryItemParam(
                                "nonce",
                                "nonce_123"
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
                                "gas",
                                "gas_123"
                            ),
                            TxHistoryItemParam(
                                "gasPrice",
                                "gasPrice_123"
                            ),
                            TxHistoryItemParam(
                                "gasUsed",
                                "gasUsed_123"
                            ),
                            TxHistoryItemParam(
                                "contractAddress",
                                contractAddress
                            )
                        )
                    )
                )
            )
            // Test Data End


            val result = historyInfoRemoteLoader.loadHistoryInfo(
                pageCount = pageCount,
                cursor = cursor,
                signAddress = signAddress,
                chainInfo = ChainInfo.Ethereum(
                    chainId = chainId,
                    contractAddress = contractAddress,
                    ethereumType = ethereumType,
                    apiKey = ""
                ),
                filters = filters
            )

            // Verification & Assertion
//            coVerify {
//                restClient.get(
//                    request = etherScanNormalRequestToMock
//                )
//            }.wasNotInvoked()
//
//            coVerify {
//                restClient.get(
//                    request = etherScanErcBepRequestToMock
//                )
//            }.wasInvoked(1)

            assertTrue { result == expectedResult }
        }

}