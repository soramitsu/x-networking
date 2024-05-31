package jp.co.soramitsu.xnetworking.lib.datasources.txhistory.adapters.reef

import io.mockative.Mock
import io.mockative.classOf
import io.mockative.coEvery
import io.mockative.coVerify
import io.mockative.mock
import jp.co.soramitsu.xnetworking.lib.datasources.chainsconfig.api.ConfigDAO
import jp.co.soramitsu.xnetworking.lib.datasources.chainsconfig.api.models.ExternalApiDAOException
import jp.co.soramitsu.xnetworking.lib.datasources.txhistory.api.models.ChainInfo
import jp.co.soramitsu.xnetworking.lib.datasources.txhistory.api.adapters.HistoryInfoRemoteLoader
import jp.co.soramitsu.xnetworking.lib.datasources.txhistory.api.utils.PackedCursor
import jp.co.soramitsu.xnetworking.lib.datasources.txhistory.api.models.TxFilter
import jp.co.soramitsu.xnetworking.lib.datasources.txhistory.api.models.TxHistoryInfo
import jp.co.soramitsu.xnetworking.lib.datasources.txhistory.api.models.TxHistoryItem
import jp.co.soramitsu.xnetworking.lib.datasources.txhistory.api.models.TxHistoryItemParam
import jp.co.soramitsu.xnetworking.lib.engines.utils.GraphQLResponseDataWrapper
import jp.co.soramitsu.xnetworking.lib.datasources.txhistory.impl.domain.adapters.reef.ReefHistoryInfoRemoteLoader
import jp.co.soramitsu.xnetworking.lib.datasources.txhistory.impl.domain.adapters.reef.ReefRequest
import jp.co.soramitsu.xnetworking.lib.datasources.txhistory.impl.domain.adapters.reef.ReefResponse
import jp.co.soramitsu.xnetworking.lib.datasources.txhistory.impl.utils.create
import jp.co.soramitsu.xnetworking.lib.engines.rest.api.RestClient
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class ReefHistoryInfoRemoteLoaderTest {

    private companion object {
        const val chainId = "reef"
        const val requestUrl = "reef.url"

        const val pageCount = 0
        const val signAddress = ""

        const val cursor = "transfers:1;rewards:1;extrinsics:1"
        val packedCursor by PackedCursor.create(cursor)
    }

    @Mock
    private val configDAO = mock(classOf<ConfigDAO>())

    @Mock
    private val restClient = mock(classOf<RestClient>())

    private val historyInfoRemoteLoader: HistoryInfoRemoteLoader =
        ReefHistoryInfoRemoteLoader(
            configDAO = configDAO,
            restClient = restClient
        )

    @Test
    fun `TEST reefHistoryInfoRemoteLoader_loadHistoryInfo EXPECT ExternalApiDAOException_NullUrl BECAUSE history url is null`() =
        runTest {
            // Test Data Start
            val filters = TxFilter.entries.toSet()

            val reefTransfersRequestToMock =
                ReefRequest(
                    url = requestUrl,
                    address = signAddress,
                    limit = pageCount,
                    cursor = packedCursor["transfers"],
                    txFilter = TxFilter.TRANSFER
                )

            val reefRewardsRequestToMock =
                ReefRequest(
                    url = requestUrl,
                    address = signAddress,
                    limit = pageCount,
                    cursor = packedCursor["rewards"],
                    txFilter = TxFilter.REWARD
                )

            val reefExtrinsicsRequestToMock =
                ReefRequest(
                    url = requestUrl,
                    address = signAddress,
                    limit = pageCount,
                    cursor = packedCursor["extrinsics"],
                    txFilter = TxFilter.EXTRINSIC
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
                    request = reefTransfersRequestToMock
                )
            }.wasNotInvoked()

            coVerify {
                restClient.get(
                    request = reefRewardsRequestToMock
                )
            }.wasNotInvoked()

            coVerify {
                restClient.get(
                    request = reefExtrinsicsRequestToMock
                )
            }.wasNotInvoked()
        }

    @Test
    fun `TEST reefHistoryInfoRemoteLoader_loadHistoryInfo EXPECT emptyList FOR empty filters set`() =
        runTest {
            // Test Data Start
            val filters = emptySet<TxFilter>()

            val reefTransfersRequestToMock =
                ReefRequest(
                    url = requestUrl,
                    address = signAddress,
                    limit = pageCount,
                    cursor = packedCursor["transfers"],
                    txFilter = TxFilter.TRANSFER
                )

            val reefTransfersResponseToReturn =
                GraphQLResponseDataWrapper(
                    data = ReefResponse(
                        transfersConnection = ReefResponse.TransfersConnection(
                            pageInfo = ReefResponse.PageInfo(
                                endCursor = "endCursor_123",
                                hasNextPage = false,
                                startCursor = packedCursor["transfers"]
                            ),
                            edges = listOf(
                                ReefResponse.TransfersConnection.Node(
                                    node = ReefResponse.TransfersConnection.Node.TransferElement(
                                        id = "id_123",
                                        amount = "amount_123",
                                        type = "type_123",
                                        timestamp = "timestamp_123",
                                        success = true,
                                        to = ReefResponse.ReefAddress(
                                            id = "to_someone"
                                        ),
                                        from = ReefResponse.ReefAddress(
                                            id = "from_someone"
                                        ),
                                        extrinsicHash = "extrinsicHash_123",
                                        signedData = ReefResponse.ReefSignedData(
                                            fee = ReefResponse.ReefSignedData.ReefFeeData(
                                                partialFee = "fee_123"
                                            )
                                        )
                                    )
                                )
                            )
                        ),
                        rewardsConnection = null,
                        extrinsicsConnection = null
                    )
                )

            val reefRewardsRequestToMock =
                ReefRequest(
                    url = requestUrl,
                    address = signAddress,
                    limit = pageCount,
                    cursor = packedCursor["rewards"],
                    txFilter = TxFilter.REWARD
                )

            val reefRewardsResponseToReturn =
                GraphQLResponseDataWrapper(
                    data = ReefResponse(
                        transfersConnection = null,
                        rewardsConnection = ReefResponse.RewardsConnection(
                            pageInfo = ReefResponse.PageInfo(
                                endCursor = "endCursor_123",
                                hasNextPage = false,
                                startCursor = packedCursor["rewards"]
                            ),
                            edges = listOf(
                                ReefResponse.RewardsConnection.Node(
                                    node = ReefResponse.RewardsConnection.Node.RewardElement(
                                        id = "id_123",
                                        type = "type_123",
                                        amount = "amount_123",
                                        timestamp = "timestamp_123",
                                        signer = ReefResponse.ReefAddress(
                                            id = "signer_123"
                                        )
                                    )
                                )
                            )
                        ),
                        extrinsicsConnection = null
                    )
                )

            val reefExtrinsicsRequestToMock =
                ReefRequest(
                    url = requestUrl,
                    address = signAddress,
                    limit = pageCount,
                    cursor = packedCursor["extrinsics"],
                    txFilter = TxFilter.EXTRINSIC
                )

            val reefExtrinsicsResponseToReturn =
                GraphQLResponseDataWrapper(
                    data = ReefResponse(
                        transfersConnection = null,
                        rewardsConnection = null,
                        extrinsicsConnection = ReefResponse.ExtrinsicsConnection(
                            pageInfo = ReefResponse.PageInfo(
                                endCursor = "endCursor_123",
                                hasNextPage = false,
                                startCursor = packedCursor["extrinsics"]
                            ),
                            edges = listOf(
                                ReefResponse.ExtrinsicsConnection.Node(
                                    node = ReefResponse.ExtrinsicsConnection.Node.ExtrinsicElement(
                                        id = "id_123",
                                        hash = "hash_123",
                                        method = "method_123",
                                        section = "section_123",
                                        signedData = ReefResponse.ReefSignedData(
                                            fee = ReefResponse.ReefSignedData.ReefFeeData(
                                                partialFee = "fee_123"
                                            )
                                        ),
                                        status = "status_123",
                                        signer = "signer_123",
                                        timestamp = "timestamp_123",
                                        type = "type_123"
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
                    request = reefTransfersRequestToMock
                )
            }.returns(reefTransfersResponseToReturn)

            coEvery {
                restClient.post(
                    request = reefRewardsRequestToMock
                )
            }.returns(reefRewardsResponseToReturn)

            coEvery {
                restClient.post(
                    request = reefExtrinsicsRequestToMock
                )
            }.returns(reefExtrinsicsResponseToReturn)
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
                    request = reefTransfersRequestToMock
                )
            }.wasNotInvoked()

            coVerify {
                restClient.post(
                    request = reefRewardsRequestToMock
                )
            }.wasNotInvoked()

            coVerify {
                restClient.post(
                    request = reefExtrinsicsRequestToMock
                )
            }.wasNotInvoked()

            assertTrue { result == expectedResult }
        }

    @Test
    fun `TEST reefHistoryInfoRemoteLoader_loadHistoryInfo EXPECT success FOR transfers AND transfer filter set`() =
        runTest {
            // Test Data Start
            val filters = setOf(TxFilter.TRANSFER)

            val reefTransfersRequestToMock =
                ReefRequest(
                    url = requestUrl,
                    address = signAddress,
                    limit = pageCount,
                    cursor = packedCursor["transfers"],
                    txFilter = TxFilter.TRANSFER
                )

            val reefTransfersResponseToReturn =
                GraphQLResponseDataWrapper(
                    data = ReefResponse(
                        transfersConnection = ReefResponse.TransfersConnection(
                            pageInfo = ReefResponse.PageInfo(
                                endCursor = "endCursor_123",
                                hasNextPage = false,
                                startCursor = packedCursor["transfers"]
                            ),
                            edges = listOf(
                                ReefResponse.TransfersConnection.Node(
                                    node = ReefResponse.TransfersConnection.Node.TransferElement(
                                        id = "id_123",
                                        amount = "amount_123",
                                        type = "type_123",
                                        timestamp = "timestamp_123",
                                        success = true,
                                        to = ReefResponse.ReefAddress(
                                            id = "to_someone"
                                        ),
                                        from = ReefResponse.ReefAddress(
                                            id = "from_someone"
                                        ),
                                        extrinsicHash = "extrinsicHash_123",
                                        signedData = ReefResponse.ReefSignedData(
                                            fee = ReefResponse.ReefSignedData.ReefFeeData(
                                                partialFee = "fee_123"
                                            )
                                        )
                                    )
                                )
                            )
                        ),
                        rewardsConnection = null,
                        extrinsicsConnection = null
                    )
                )

            val reefRewardsRequestToMock =
                ReefRequest(
                    url = requestUrl,
                    address = signAddress,
                    limit = pageCount,
                    cursor = packedCursor["rewards"],
                    txFilter = TxFilter.REWARD
                )

            val reefRewardsResponseToReturn =
                GraphQLResponseDataWrapper(
                    data = ReefResponse(
                        transfersConnection = null,
                        rewardsConnection = ReefResponse.RewardsConnection(
                            pageInfo = ReefResponse.PageInfo(
                                endCursor = "endCursor_123",
                                hasNextPage = false,
                                startCursor = packedCursor["rewards"]
                            ),
                            edges = listOf(
                                ReefResponse.RewardsConnection.Node(
                                    node = ReefResponse.RewardsConnection.Node.RewardElement(
                                        id = "id_123",
                                        type = "type_123",
                                        amount = "amount_123",
                                        timestamp = "timestamp_123",
                                        signer = ReefResponse.ReefAddress(
                                            id = "signer_123"
                                        )
                                    )
                                )
                            )
                        ),
                        extrinsicsConnection = null
                    )
                )

            val reefExtrinsicsRequestToMock =
                ReefRequest(
                    url = requestUrl,
                    address = signAddress,
                    limit = pageCount,
                    cursor = packedCursor["extrinsics"],
                    txFilter = TxFilter.EXTRINSIC
                )

            val reefExtrinsicsResponseToReturn =
                GraphQLResponseDataWrapper(
                    data = ReefResponse(
                        transfersConnection = null,
                        rewardsConnection = null,
                        extrinsicsConnection = ReefResponse.ExtrinsicsConnection(
                            pageInfo = ReefResponse.PageInfo(
                                endCursor = "endCursor_123",
                                hasNextPage = false,
                                startCursor = packedCursor["extrinsics"]
                            ),
                            edges = listOf(
                                ReefResponse.ExtrinsicsConnection.Node(
                                    node = ReefResponse.ExtrinsicsConnection.Node.ExtrinsicElement(
                                        id = "id_123",
                                        hash = "hash_123",
                                        method = "method_123",
                                        section = "section_123",
                                        signedData = ReefResponse.ReefSignedData(
                                            fee = ReefResponse.ReefSignedData.ReefFeeData(
                                                partialFee = "fee_123"
                                            )
                                        ),
                                        status = "status_123",
                                        signer = "signer_123",
                                        timestamp = "timestamp_123",
                                        type = "type_123"
                                    )
                                )
                            )
                        )
                    )
                )

            val expectedResult = TxHistoryInfo(
                endCursor = "transfers:endCursor_123;rewards:1;extrinsics:1",
                endReached = true,
                items = listOf(
                    TxHistoryItem(
                        id = "id_123",
                        blockHash = "extrinsicHash_123",
                        module = "transfer",
                        method = "",
                        timestamp = "timestamp_123",
                        nestedData = emptyList(),
                        networkFee = "fee_123",
                        success = true,
                        data = listOfNotNull(
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
                    request = reefTransfersRequestToMock
                )
            }.returns(reefTransfersResponseToReturn)

            coEvery {
                restClient.post(
                    request = reefRewardsRequestToMock
                )
            }.returns(reefRewardsResponseToReturn)

            coEvery {
                restClient.post(
                    request = reefExtrinsicsRequestToMock
                )
            }.returns(reefExtrinsicsResponseToReturn)
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
                    request = reefTransfersRequestToMock
                )
            }.wasInvoked(1)

            coVerify {
                restClient.post(
                    request = reefRewardsRequestToMock
                )
            }.wasNotInvoked()

            coVerify {
                restClient.post(
                    request = reefExtrinsicsRequestToMock
                )
            }.wasNotInvoked()

            assertTrue { result == expectedResult }
        }

    @Test
    fun `TEST reefHistoryInfoRemoteLoader_loadHistoryInfo EXPECT success FOR rewards AND reward filter set`() =
        runTest {
            // Test Data Start
            val filters = setOf(TxFilter.REWARD)

            val reefTransfersRequestToMock =
                ReefRequest(
                    url = requestUrl,
                    address = signAddress,
                    limit = pageCount,
                    cursor = packedCursor["transfers"],
                    txFilter = TxFilter.TRANSFER
                )

            val reefTransfersResponseToReturn =
                GraphQLResponseDataWrapper(
                    data = ReefResponse(
                        transfersConnection = ReefResponse.TransfersConnection(
                            pageInfo = ReefResponse.PageInfo(
                                endCursor = "endCursor_123",
                                hasNextPage = false,
                                startCursor = packedCursor["transfers"]
                            ),
                            edges = listOf(
                                ReefResponse.TransfersConnection.Node(
                                    node = ReefResponse.TransfersConnection.Node.TransferElement(
                                        id = "id_123",
                                        amount = "amount_123",
                                        type = "type_123",
                                        timestamp = "timestamp_123",
                                        success = true,
                                        to = ReefResponse.ReefAddress(
                                            id = "to_someone"
                                        ),
                                        from = ReefResponse.ReefAddress(
                                            id = "from_someone"
                                        ),
                                        extrinsicHash = "extrinsicHash_123",
                                        signedData = ReefResponse.ReefSignedData(
                                            fee = ReefResponse.ReefSignedData.ReefFeeData(
                                                partialFee = "fee_123"
                                            )
                                        )
                                    )
                                )
                            )
                        ),
                        rewardsConnection = null,
                        extrinsicsConnection = null
                    )
                )

            val reefRewardsRequestToMock =
                ReefRequest(
                    url = requestUrl,
                    address = signAddress,
                    limit = pageCount,
                    cursor = packedCursor["rewards"],
                    txFilter = TxFilter.REWARD
                )

            val reefRewardsResponseToReturn =
                GraphQLResponseDataWrapper(
                    data = ReefResponse(
                        transfersConnection = null,
                        rewardsConnection = ReefResponse.RewardsConnection(
                            pageInfo = ReefResponse.PageInfo(
                                endCursor = "endCursor_123",
                                hasNextPage = false,
                                startCursor = packedCursor["rewards"]
                            ),
                            edges = listOf(
                                ReefResponse.RewardsConnection.Node(
                                    node = ReefResponse.RewardsConnection.Node.RewardElement(
                                        id = "id_123",
                                        type = "type_123",
                                        amount = "amount_123",
                                        timestamp = "timestamp_123",
                                        signer = ReefResponse.ReefAddress(
                                            id = "signer_123"
                                        )
                                    )
                                )
                            )
                        ),
                        extrinsicsConnection = null
                    )
                )

            val reefExtrinsicsRequestToMock =
                ReefRequest(
                    url = requestUrl,
                    address = signAddress,
                    limit = pageCount,
                    cursor = packedCursor["extrinsics"],
                    txFilter = TxFilter.EXTRINSIC
                )

            val reefExtrinsicsResponseToReturn =
                GraphQLResponseDataWrapper(
                    data = ReefResponse(
                        transfersConnection = null,
                        rewardsConnection = null,
                        extrinsicsConnection = ReefResponse.ExtrinsicsConnection(
                            pageInfo = ReefResponse.PageInfo(
                                endCursor = "endCursor_123",
                                hasNextPage = false,
                                startCursor = packedCursor["extrinsics"]
                            ),
                            edges = listOf(
                                ReefResponse.ExtrinsicsConnection.Node(
                                    node = ReefResponse.ExtrinsicsConnection.Node.ExtrinsicElement(
                                        id = "id_123",
                                        hash = "hash_123",
                                        method = "method_123",
                                        section = "section_123",
                                        signedData = ReefResponse.ReefSignedData(
                                            fee = ReefResponse.ReefSignedData.ReefFeeData(
                                                partialFee = "fee_123"
                                            )
                                        ),
                                        status = "status_123",
                                        signer = "signer_123",
                                        timestamp = "timestamp_123",
                                        type = "type_123"
                                    )
                                )
                            )
                        )
                    )
                )

            val expectedResult = TxHistoryInfo(
                endCursor = "transfers:1;rewards:endCursor_123;extrinsics:1",
                endReached = true,
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
                        data = listOfNotNull(
                            TxHistoryItemParam(
                                "accountId",
                                "signer_123"
                            ),
                            TxHistoryItemParam(
                                "amount",
                                "amount_123"
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
                restClient.post(
                    request = reefTransfersRequestToMock
                )
            }.returns(reefTransfersResponseToReturn)

            coEvery {
                restClient.post(
                    request = reefRewardsRequestToMock
                )
            }.returns(reefRewardsResponseToReturn)

            coEvery {
                restClient.post(
                    request = reefExtrinsicsRequestToMock
                )
            }.returns(reefExtrinsicsResponseToReturn)
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
                    request = reefTransfersRequestToMock
                )
            }.wasNotInvoked()

            coVerify {
                restClient.post(
                    request = reefRewardsRequestToMock
                )
            }.wasInvoked(1)

            coVerify {
                restClient.post(
                    request = reefExtrinsicsRequestToMock
                )
            }.wasNotInvoked()

            assertTrue { result == expectedResult }
        }

    @Test
    fun `TEST reefHistoryInfoRemoteLoader_loadHistoryInfo EXPECT success FOR extrinsics AND extrinsic filter set`() =
        runTest {
            // Test Data Start
            val filters = setOf(TxFilter.EXTRINSIC)

            val reefTransfersRequestToMock =
                ReefRequest(
                    url = requestUrl,
                    address = signAddress,
                    limit = pageCount,
                    cursor = packedCursor["transfers"],
                    txFilter = TxFilter.TRANSFER
                )

            val reefTransfersResponseToReturn =
                GraphQLResponseDataWrapper(
                    data = ReefResponse(
                        transfersConnection = ReefResponse.TransfersConnection(
                            pageInfo = ReefResponse.PageInfo(
                                endCursor = "endCursor_123",
                                hasNextPage = false,
                                startCursor = packedCursor["transfers"]
                            ),
                            edges = listOf(
                                ReefResponse.TransfersConnection.Node(
                                    node = ReefResponse.TransfersConnection.Node.TransferElement(
                                        id = "id_123",
                                        amount = "amount_123",
                                        type = "type_123",
                                        timestamp = "timestamp_123",
                                        success = true,
                                        to = ReefResponse.ReefAddress(
                                            id = "to_someone"
                                        ),
                                        from = ReefResponse.ReefAddress(
                                            id = "from_someone"
                                        ),
                                        extrinsicHash = "extrinsicHash_123",
                                        signedData = ReefResponse.ReefSignedData(
                                            fee = ReefResponse.ReefSignedData.ReefFeeData(
                                                partialFee = "fee_123"
                                            )
                                        )
                                    )
                                )
                            )
                        ),
                        rewardsConnection = null,
                        extrinsicsConnection = null
                    )
                )

            val reefRewardsRequestToMock =
                ReefRequest(
                    url = requestUrl,
                    address = signAddress,
                    limit = pageCount,
                    cursor = packedCursor["rewards"],
                    txFilter = TxFilter.REWARD
                )

            val reefRewardsResponseToReturn =
                GraphQLResponseDataWrapper(
                    data = ReefResponse(
                        transfersConnection = null,
                        rewardsConnection = ReefResponse.RewardsConnection(
                            pageInfo = ReefResponse.PageInfo(
                                endCursor = "endCursor_123",
                                hasNextPage = false,
                                startCursor = packedCursor["rewards"]
                            ),
                            edges = listOf(
                                ReefResponse.RewardsConnection.Node(
                                    node = ReefResponse.RewardsConnection.Node.RewardElement(
                                        id = "id_123",
                                        type = "type_123",
                                        amount = "amount_123",
                                        timestamp = "timestamp_123",
                                        signer = ReefResponse.ReefAddress(
                                            id = "signer_123"
                                        )
                                    )
                                )
                            )
                        ),
                        extrinsicsConnection = null
                    )
                )

            val reefExtrinsicsRequestToMock =
                ReefRequest(
                    url = requestUrl,
                    address = signAddress,
                    limit = pageCount,
                    cursor = packedCursor["extrinsics"],
                    txFilter = TxFilter.EXTRINSIC
                )

            val reefExtrinsicsResponseToReturn =
                GraphQLResponseDataWrapper(
                    data = ReefResponse(
                        transfersConnection = null,
                        rewardsConnection = null,
                        extrinsicsConnection = ReefResponse.ExtrinsicsConnection(
                            pageInfo = ReefResponse.PageInfo(
                                endCursor = "endCursor_123",
                                hasNextPage = false,
                                startCursor = packedCursor["extrinsics"]
                            ),
                            edges = listOf(
                                ReefResponse.ExtrinsicsConnection.Node(
                                    node = ReefResponse.ExtrinsicsConnection.Node.ExtrinsicElement(
                                        id = "id_123",
                                        hash = "hash_123",
                                        method = "method_123",
                                        section = "section_123",
                                        signedData = ReefResponse.ReefSignedData(
                                            fee = ReefResponse.ReefSignedData.ReefFeeData(
                                                partialFee = "fee_123"
                                            )
                                        ),
                                        status = "status_123",
                                        signer = "signer_123",
                                        timestamp = "timestamp_123",
                                        type = "type_123"
                                    )
                                )
                            )
                        )
                    )
                )

            val expectedResult = TxHistoryInfo(
                endCursor = "transfers:1;rewards:1;extrinsics:endCursor_123",
                endReached = true,
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
                        data = listOfNotNull(
                            TxHistoryItemParam(
                                "accountId",
                                "signer_123"
                            ),
                            TxHistoryItemParam(
                                "section",
                                "section_123"
                            ),
                            TxHistoryItemParam(
                                "method",
                                "method_123"
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
                restClient.post(
                    request = reefTransfersRequestToMock
                )
            }.returns(reefTransfersResponseToReturn)

            coEvery {
                restClient.post(
                    request = reefRewardsRequestToMock
                )
            }.returns(reefRewardsResponseToReturn)

            coEvery {
                restClient.post(
                    request = reefExtrinsicsRequestToMock
                )
            }.returns(reefExtrinsicsResponseToReturn)
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
                    request = reefTransfersRequestToMock
                )
            }.wasNotInvoked()

            coVerify {
                restClient.post(
                    request = reefRewardsRequestToMock
                )
            }.wasNotInvoked()

            coVerify {
                restClient.post(
                    request = reefExtrinsicsRequestToMock
                )
            }.wasInvoked(1)

            assertTrue { result == expectedResult }
        }

}