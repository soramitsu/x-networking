package jp.co.soramitsu.xnetworking.lib.datasources.txhistory.adapters.subquery

import io.mockative.Mock
import io.mockative.classOf
import io.mockative.coEvery
import io.mockative.coVerify
import io.mockative.mock
import jp.co.soramitsu.xnetworking.lib.datasources.chainsconfig.api.ConfigDAO
import jp.co.soramitsu.xnetworking.lib.datasources.chainsconfig.api.models.ExternalApiDAOException
import jp.co.soramitsu.xnetworking.lib.datasources.chainsconfig.api.models.StakingOption
import jp.co.soramitsu.xnetworking.lib.datasources.txhistory.api.models.ChainInfo
import jp.co.soramitsu.xnetworking.lib.datasources.txhistory.api.adapters.HistoryInfoRemoteLoader
import jp.co.soramitsu.xnetworking.lib.datasources.txhistory.api.models.TxFilter
import jp.co.soramitsu.xnetworking.lib.datasources.txhistory.api.models.TxHistoryInfo
import jp.co.soramitsu.xnetworking.lib.datasources.txhistory.api.models.TxHistoryItem
import jp.co.soramitsu.xnetworking.lib.datasources.txhistory.api.models.TxHistoryItemParam
import jp.co.soramitsu.xnetworking.lib.engines.utils.GraphQLResponseDataWrapper
import jp.co.soramitsu.xnetworking.lib.datasources.txhistory.impl.domain.adapters.subquery.SubQueryHistoryInfoRemoteLoader
import jp.co.soramitsu.xnetworking.lib.datasources.txhistory.impl.domain.adapters.subquery.SubQueryRequest
import jp.co.soramitsu.xnetworking.lib.datasources.txhistory.impl.domain.adapters.subquery.SubQueryResponse
import jp.co.soramitsu.xnetworking.lib.engines.rest.api.RestClient
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class SubQueryHistoryInfoRemoteLoaderTest {

    private companion object {
        const val chainId = "quartz"
        const val requestUrl = "quartz.url"

        const val cursor = ""
        const val pageCount = 0
        const val signAddress = ""
    }

    @Mock
    private val configDAO = mock(classOf<ConfigDAO>())

    @Mock
    private val restClient = mock(classOf<RestClient>())

    private val historyInfoRemoteLoader: HistoryInfoRemoteLoader =
        SubQueryHistoryInfoRemoteLoader(
            configDAO = configDAO,
            restClient = restClient
        )

    @Test
    fun `TEST subQueryHistoryInfoRemoteLoader_loadHistoryInfo EXPECT ExternalApiDAOException_NullUrl BECAUSE history url is null`() =
        runTest {
            // Test Data Start
            val filters = TxFilter.entries.toSet()

            val subQueryRequestToMock =
                SubQueryRequest(
                    url = requestUrl,
                    address = signAddress,
                    cursor = cursor,
                    pageCount = pageCount,
                    filters = filters,
                    requestRewards = true
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
                    request = subQueryRequestToMock
                )
            }.wasNotInvoked()
        }

    @Test
    fun `TEST subQueryHistoryInfoRemoteLoader_loadHistoryInfo EXPECT emptyList FOR empty filters set`() =
        runTest {
            // Test Data Start
            val filters = emptySet<TxFilter>()

            val subQueryRequestToMock =
                SubQueryRequest(
                    url = requestUrl,
                    address = signAddress,
                    cursor = cursor,
                    pageCount = pageCount,
                    filters = filters,
                    requestRewards = true
                )

            val subQueryResponseToReturn =
                GraphQLResponseDataWrapper(
                    data = SubQueryResponse(
                        historyElements = SubQueryResponse.HistoryElements(
                            pageInfo = SubQueryResponse.HistoryElements.PageInfo(
                                startCursor = null,
                                endCursor = null
                            ),
                            nodes = arrayOf(
                                SubQueryResponse.HistoryElements.Node(
                                    id = "id_123",
                                    timestamp = "timestamp_123",
                                    address = "address_123",
                                    reward = SubQueryResponse.HistoryElements.Node.Rewards(
                                        era = 123,
                                        amount = "amount_123",
                                        isReward = true,
                                        validator = "validator_123",
                                        assetId = "assetId_123"
                                    ),
                                    transfer = SubQueryResponse.HistoryElements.Node.Transfer(
                                        amount = "amount_123",
                                        to = "to_someone",
                                        from = "from_someone",
                                        fee = "fee_123",
                                        block = "block_123",
                                        success = true,
                                        extrinsicHash = "extrinsicHash_123",
                                        assetId = "asset_123"
                                    ),
                                    extrinsic = SubQueryResponse.HistoryElements.Node.Extrinsic(
                                        hash = "hash_123",
                                        module = "module_123",
                                        call = "call_123",
                                        fee = "fee_123",
                                        success = true,
                                        assetId = "assetId_123"
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
                configDAO.staking(
                    chainId = chainId
                )
            }.returns(StakingOption.RELAYCHAIN)

            coEvery {
                restClient.post(
                    request = subQueryRequestToMock
                )
            }.returns(subQueryResponseToReturn)
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
                    request = subQueryRequestToMock
                )
            }.wasNotInvoked()

            assertTrue { result == expectedResult }
        }

    @Test
    fun `TEST subQueryHistoryInfoRemoteLoader_loadHistoryInfo EXPECT success FOR transfers AND transfer filter`() =
        runTest {
            // Test Data Start
            val filters = setOf(TxFilter.TRANSFER)

            val subQueryRequestToMock =
                SubQueryRequest(
                    url = requestUrl,
                    address = signAddress,
                    cursor = cursor,
                    pageCount = pageCount,
                    filters = filters,
                    requestRewards = true
                )

            val subQueryResponseToReturn =
                GraphQLResponseDataWrapper(
                    data = SubQueryResponse(
                        historyElements = SubQueryResponse.HistoryElements(
                            pageInfo = SubQueryResponse.HistoryElements.PageInfo(
                                startCursor = cursor,
                                endCursor = "endCursor_123"
                            ),
                            nodes = arrayOf(
                                SubQueryResponse.HistoryElements.Node(
                                    id = "id_123",
                                    timestamp = "timestamp_123",
                                    address = "address_123",
                                    reward = SubQueryResponse.HistoryElements.Node.Rewards(
                                        era = 123,
                                        amount = "amount_123",
                                        isReward = true,
                                        validator = "validator_123",
                                        assetId = "assetId_123"
                                    ),
                                    transfer = SubQueryResponse.HistoryElements.Node.Transfer(
                                        amount = "amount_123",
                                        to = "to_someone",
                                        from = "from_someone",
                                        fee = "fee_123",
                                        block = "block_123",
                                        success = true,
                                        extrinsicHash = "extrinsicHash_123",
                                        assetId = "asset_123"
                                    ),
                                    extrinsic = SubQueryResponse.HistoryElements.Node.Extrinsic(
                                        hash = "hash_123",
                                        module = "module_123",
                                        call = "call_123",
                                        fee = "fee_123",
                                        success = true,
                                        assetId = "assetId_123"
                                    )
                                )
                            )
                        )
                    )
                )

            val expectedResult = TxHistoryInfo(
                endCursor = "endCursor_123",
                endReached = false,
                items = listOf(
                    TxHistoryItem(
                        id = "id_123",
                        blockHash = "block_123",
                        module = "transfer",
                        method = "",
                        timestamp = "timestamp_123",
                        nestedData = emptyList(),
                        networkFee = "fee_123",
                        success = true,
                        data = listOf(
                            TxHistoryItemParam(
                                "tokenId",
                                "asset_123"
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
                configDAO.staking(
                    chainId = chainId
                )
            }.returns(StakingOption.RELAYCHAIN)

            coEvery {
                restClient.post(
                    request = subQueryRequestToMock
                )
            }.returns(subQueryResponseToReturn)
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
                    request = subQueryRequestToMock
                )
            }.wasInvoked(1)

            assertTrue { result == expectedResult }
        }

    @Test
    fun `TEST subQueryHistoryInfoRemoteLoader_loadHistoryInfo EXPECT success FOR rewards AND reward filter`() =
        runTest {
            // Test Data Start
            val filters = setOf(TxFilter.REWARD)

            val subQueryRequestToMock =
                SubQueryRequest(
                    url = requestUrl,
                    address = signAddress,
                    cursor = cursor,
                    pageCount = pageCount,
                    filters = filters,
                    requestRewards = true
                )

            val subQueryResponseToReturn =
                GraphQLResponseDataWrapper(
                    data = SubQueryResponse(
                        historyElements = SubQueryResponse.HistoryElements(
                            pageInfo = SubQueryResponse.HistoryElements.PageInfo(
                                startCursor = cursor,
                                endCursor = "endCursor_123"
                            ),
                            nodes = arrayOf(
                                SubQueryResponse.HistoryElements.Node(
                                    id = "id_123",
                                    timestamp = "timestamp_123",
                                    address = "address_123",
                                    reward = SubQueryResponse.HistoryElements.Node.Rewards(
                                        era = 123,
                                        amount = "amount_123",
                                        isReward = true,
                                        validator = "validator_123",
                                        assetId = "assetId_123"
                                    ),
                                    transfer = SubQueryResponse.HistoryElements.Node.Transfer(
                                        amount = "amount_123",
                                        to = "to_someone",
                                        from = "from_someone",
                                        fee = "fee_123",
                                        block = "block_123",
                                        success = true,
                                        extrinsicHash = "extrinsicHash_123",
                                        assetId = "asset_123"
                                    ),
                                    extrinsic = SubQueryResponse.HistoryElements.Node.Extrinsic(
                                        hash = "hash_123",
                                        module = "module_123",
                                        call = "call_123",
                                        fee = "fee_123",
                                        success = true,
                                        assetId = "assetId_123"
                                    )
                                )
                            )
                        )
                    )
                )

            val expectedResult = TxHistoryInfo(
                endCursor = "endCursor_123",
                endReached = false,
                items = listOf(
                    TxHistoryItem(
                        id = "id_123",
                        blockHash = "",
                        module = "reward",
                        method = "",
                        timestamp = "timestamp_123",
                        nestedData = emptyList(),
                        networkFee = "0",
                        success = true,
                        data = listOf(
                            TxHistoryItemParam(
                                "tokenId",
                                "assetId_123"
                            ),
                            TxHistoryItemParam(
                                "amount",
                                "amount_123"
                            ),
                            TxHistoryItemParam(
                                "validator",
                                "validator_123"
                            ),
                            TxHistoryItemParam(
                                "isReward",
                                "true"
                            ),
                            TxHistoryItemParam(
                                "era",
                                "123"
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
                configDAO.staking(
                    chainId = chainId
                )
            }.returns(StakingOption.RELAYCHAIN)

            coEvery {
                restClient.post(
                    request = subQueryRequestToMock
                )
            }.returns(subQueryResponseToReturn)
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
                    request = subQueryRequestToMock
                )
            }.wasInvoked(1)

            assertTrue { result == expectedResult }
        }

    @Test
    fun `TEST subQueryHistoryInfoRemoteLoader_loadHistoryInfo EXPECT success FOR extrinsic AND reward filter`() =
        runTest {
            // Test Data Start
            val filters = setOf(TxFilter.EXTRINSIC)

            val subQueryRequestToMock =
                SubQueryRequest(
                    url = requestUrl,
                    address = signAddress,
                    cursor = cursor,
                    pageCount = pageCount,
                    filters = filters,
                    requestRewards = true
                )

            val subQueryResponseToReturn =
                GraphQLResponseDataWrapper(
                    data = SubQueryResponse(
                        historyElements = SubQueryResponse.HistoryElements(
                            pageInfo = SubQueryResponse.HistoryElements.PageInfo(
                                startCursor = cursor,
                                endCursor = "endCursor_123"
                            ),
                            nodes = arrayOf(
                                SubQueryResponse.HistoryElements.Node(
                                    id = "id_123",
                                    timestamp = "timestamp_123",
                                    address = "address_123",
                                    reward = SubQueryResponse.HistoryElements.Node.Rewards(
                                        era = 123,
                                        amount = "amount_123",
                                        isReward = true,
                                        validator = "validator_123",
                                        assetId = "assetId_123"
                                    ),
                                    transfer = SubQueryResponse.HistoryElements.Node.Transfer(
                                        amount = "amount_123",
                                        to = "to_someone",
                                        from = "from_someone",
                                        fee = "fee_123",
                                        block = "block_123",
                                        success = true,
                                        extrinsicHash = "extrinsicHash_123",
                                        assetId = "asset_123"
                                    ),
                                    extrinsic = SubQueryResponse.HistoryElements.Node.Extrinsic(
                                        hash = "hash_123",
                                        module = "module_123",
                                        call = "call_123",
                                        fee = "fee_123",
                                        success = true,
                                        assetId = "assetId_123"
                                    )
                                )
                            )
                        )
                    )
                )

            val expectedResult = TxHistoryInfo(
                endCursor = "endCursor_123",
                endReached = false,
                items = listOf(
                    TxHistoryItem(
                        id = "id_123",
                        blockHash = "hash_123",
                        module = "extrinsic",
                        method = "",
                        timestamp = "timestamp_123",
                        nestedData = emptyList(),
                        networkFee = "fee_123",
                        success = true,
                        data = listOf(
                            TxHistoryItemParam(
                                "tokenId",
                                "assetId_123"
                            ),
                            TxHistoryItemParam(
                                "module",
                                "module_123"
                            ),
                            TxHistoryItemParam(
                                "call",
                                "call_123"
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
                configDAO.staking(
                    chainId = chainId
                )
            }.returns(StakingOption.RELAYCHAIN)

            coEvery {
                restClient.post(
                    request = subQueryRequestToMock
                )
            }.returns(subQueryResponseToReturn)
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
                    request = subQueryRequestToMock
                )
            }.wasInvoked(1)

            assertTrue { result == expectedResult }
        }

}