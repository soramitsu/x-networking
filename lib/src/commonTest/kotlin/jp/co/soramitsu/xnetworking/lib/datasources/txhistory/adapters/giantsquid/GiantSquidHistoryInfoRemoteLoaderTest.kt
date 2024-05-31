package jp.co.soramitsu.xnetworking.lib.datasources.txhistory.adapters.giantsquid

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
import jp.co.soramitsu.xnetworking.lib.datasources.txhistory.impl.domain.adapters.giantsquid.GiantSquidHistoryInfoRemoteLoader
import jp.co.soramitsu.xnetworking.lib.datasources.txhistory.impl.domain.adapters.giantsquid.GiantSquidRequest
import jp.co.soramitsu.xnetworking.lib.datasources.txhistory.impl.domain.adapters.giantsquid.GiantSquidResponse
import jp.co.soramitsu.xnetworking.lib.engines.rest.api.RestClient
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class GiantSquidHistoryInfoRemoteLoaderTest {

    private companion object {
        const val chainId = "kusama"
        const val requestUrl = "kusama.url"

        const val cursor = ""
        const val pageCount = 0
        const val signAddress = ""
    }

    @Mock
    private val configDAO = mock(classOf<ConfigDAO>())

    @Mock
    private val restClient = mock(classOf<RestClient>())

    private val historyInfoRemoteLoader: HistoryInfoRemoteLoader =
        GiantSquidHistoryInfoRemoteLoader(
            configDAO = configDAO,
            restClient = restClient
        )

    @Test
    fun `TEST giantSquidHistoryInfoRemoteLoader_loadHistoryInfo EXPECT ExternalApiDAOException_NullUrl BECAUSE history url is null`() =
        runTest {
            // Test Data Start
            val filters = TxFilter.entries.toSet()

            val giantSquidRequestToMock =
                GiantSquidRequest(
                    url = requestUrl,
                    address = signAddress,
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
                    request = giantSquidRequestToMock
                )
            }.wasNotInvoked()
        }

    @Test
    fun `TEST giantSquidHistoryInfoRemoteLoader_loadHistoryInfo EXPECT emptyList FOR empty filters set`() =
        runTest {
            // Test Data Start
            val filters = emptySet<TxFilter>()

            val giantSquidRequestToMock =
                GiantSquidRequest(
                    url = requestUrl,
                    address = signAddress,
                )

            val giantSquidResponseToReturn =
                GraphQLResponseDataWrapper(
                    data = GiantSquidResponse(
                        transfers = listOf(
                            GiantSquidResponse.TransferIdWrapper(
                                id = "id_123_transfer",
                                transfer = GiantSquidResponse.TransferIdWrapper.Transfer(
                                    amount = "amount_123_transfer",
                                    blockNumber = "blockNumber_123_transfer",
                                    extrinsicHash = "extrinsicHash_123_transfer",
                                    from = GiantSquidResponse.GiantSquidAccount(
                                        id = "from_someone_transfer"
                                    ),
                                    to = GiantSquidResponse.GiantSquidAccount(
                                        id = "to_someone_transfer"
                                    ),
                                    timestamp = "timestamp_123_transfer",
                                    success = true,
                                    id = "id_123_transfer",
                                )
                            )
                        ),
                        rewards = listOf(
                            GiantSquidResponse.Reward(
                                id = "id_123_reward",
                                timestamp = "timestamp_123_reward",
                                blockNumber = 123,
                                extrinsicHash = "extrinsicHash_123_reward",
                                amount = "amount_123_reward",
                                era = "era_123_reward",
                                validatorId = "validatorId_reward",
                                account = GiantSquidResponse.GiantSquidAccount(
                                    id = "accountId_reward"
                                )
                            )
                        ),
                        bonds = listOf(
                            GiantSquidResponse.Bond(
                                id = "id_123_bond",
                                accountId = "accountId_123_bond",
                                amount = "amount_123_bond",
                                blockNumber = "blockNumber_123_bond",
                                extrinsicHash = "extrinsicHash_123_bond",
                                success = true,
                                timestamp = "timestamp_123_bond",
                                type = "type_123_bond"
                            )
                        ),
                        slashes = listOf(
                            GiantSquidResponse.Slash(
                                id = "id_123_slash",
                                accountId = "accountId_123_slash",
                                amount = "amount_123_slash",
                                blockNumber = "blockNumber_123_slash",
                                era = "era_123_slash",
                                timestamp = "timestamp_123_slash"
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
                    request = giantSquidRequestToMock
                )
            }.returns(giantSquidResponseToReturn)
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
                    request = giantSquidRequestToMock
                )
            }.wasNotInvoked()

            assertTrue { result == expectedResult }
        }

    @Test
    fun `TEST giantSquidHistoryInfoRemoteLoader_loadHistoryInfo EXPECT success FOR transfers AND transfer filter set`() =
        runTest {
            // Test Data Start
            val filters = setOf(TxFilter.TRANSFER)

            val giantSquidRequestToMock =
                GiantSquidRequest(
                    url = requestUrl,
                    address = signAddress,
                )

            val giantSquidResponseToReturn =
                GraphQLResponseDataWrapper(
                    data = GiantSquidResponse(
                        transfers = listOf(
                            GiantSquidResponse.TransferIdWrapper(
                                id = "id_123",
                                transfer = GiantSquidResponse.TransferIdWrapper.Transfer(
                                    amount = "amount_123",
                                    blockNumber = "blockNumber_123",
                                    extrinsicHash = "extrinsicHash_123",
                                    from = GiantSquidResponse.GiantSquidAccount(
                                        id = "from_someone"
                                    ),
                                    to = GiantSquidResponse.GiantSquidAccount(
                                        id = "to_someone"
                                    ),
                                    timestamp = "timestamp_123",
                                    success = true,
                                    id = "id_123",
                                )
                            )
                        ),
                        rewards = null,
                        bonds = null,
                        slashes = null
                    )
                )

            val expectedResult = TxHistoryInfo(
                endCursor = null,
                endReached = true,
                items = listOf(
                    TxHistoryItem(
                        id = "id_123",
                        blockHash = "blockNumber_123",
                        module = "transfer",
                        method = "",
                        timestamp = "timestamp_123",
                        nestedData = emptyList(),
                        networkFee = "",
                        success = true,
                        data = listOfNotNull(
                            TxHistoryItemParam(
                                "to",
                                "to_someone"
                            ),
                            TxHistoryItemParam(
                                "from",
                                "from_someone"
                            ),
                            TxHistoryItemParam(
                                "amount",
                                "amount_123"
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
                restClient.post(
                    request = giantSquidRequestToMock
                )
            }.returns(giantSquidResponseToReturn)
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
                    request = giantSquidRequestToMock
                )
            }.wasInvoked(1)

            assertTrue { result == expectedResult }
        }

    @Test
    fun `TEST giantSquidHistoryInfoRemoteLoader_loadHistoryInfo EXPECT success FOR rewards AND reward filter set`() =
        runTest {
            // Test Data Start
            val filters = setOf(TxFilter.REWARD)

            val giantSquidRequestToMock =
                GiantSquidRequest(
                    url = requestUrl,
                    address = signAddress,
                )

            val giantSquidResponseToReturn =
                GraphQLResponseDataWrapper(
                    data = GiantSquidResponse(
                        transfers = null,
                        rewards = listOf(
                            GiantSquidResponse.Reward(
                                id = "id_123",
                                timestamp = "timestamp_123",
                                blockNumber = 123,
                                extrinsicHash = "extrinsicHash_123",
                                amount = "amount_123",
                                era = "era_123",
                                validatorId = "validatorId",
                                account = GiantSquidResponse.GiantSquidAccount(
                                    id = "accountId"
                                )
                            )
                        ),
                        bonds = null,
                        slashes = null
                    )
                )

            val expectedResult = TxHistoryInfo(
                endCursor = null,
                endReached = true,
                items = listOf(
                    TxHistoryItem(
                        id = "id_123",
                        blockHash = "123",
                        module = "reward",
                        method = "",
                        timestamp = "timestamp_123",
                        nestedData = emptyList(),
                        networkFee = "0",
                        success = true,
                        data = listOfNotNull(
                            TxHistoryItemParam(
                                "amount",
                                "amount_123"
                            ),
                            TxHistoryItemParam(
                                "extrinsicHash",
                                "extrinsicHash_123"
                            ),
                            TxHistoryItemParam(
                                "era",
                                "era_123"
                            ),
                            TxHistoryItemParam(
                                "accountId",
                                "accountId"
                            ),
                            TxHistoryItemParam(
                                "validatorId",
                                "validatorId"
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
                    request = giantSquidRequestToMock
                )
            }.returns(giantSquidResponseToReturn)
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
                    request = giantSquidRequestToMock
                )
            }.wasInvoked(1)

            assertTrue { result == expectedResult }
        }

    @Test
    fun `TEST giantSquidHistoryInfoRemoteLoader_loadHistoryInfo EXPECT success FOR bonds and slashed AND extrinsic filter set`() =
        runTest {
            // Test Data Start
            val filters = setOf(TxFilter.EXTRINSIC)

            val giantSquidRequestToMock =
                GiantSquidRequest(
                    url = requestUrl,
                    address = signAddress,
                )

            val giantSquidResponseToReturn =
                GraphQLResponseDataWrapper(
                    data = GiantSquidResponse(
                        transfers = null,
                        rewards = null,
                        bonds = listOf(
                            GiantSquidResponse.Bond(
                                id = "id_123_bond",
                                accountId = "accountId_123_bond",
                                amount = "amount_123_bond",
                                blockNumber = "blockNumber_123_bond",
                                extrinsicHash = "extrinsicHash_123_bond",
                                success = true,
                                timestamp = "timestamp_123_bond",
                                type = "type_123_bond"
                            )
                        ),
                        slashes = listOf(
                            GiantSquidResponse.Slash(
                                id = "id_123_slash",
                                accountId = "accountId_123_slash",
                                amount = "amount_123_slash",
                                blockNumber = "blockNumber_123_slash",
                                era = "era_123_slash",
                                timestamp = "timestamp_123_slash"
                            )
                        )
                    )
                )

            val expectedResult = TxHistoryInfo(
                endCursor = null,
                endReached = true,
                items = listOf(
                    TxHistoryItem(
                        id = "id_123_bond",
                        blockHash = "blockNumber_123_bond",
                        module = "bond",
                        method = "",
                        timestamp = "timestamp_123_bond",
                        nestedData = emptyList(),
                        networkFee = "0",
                        success = true,
                        data = listOfNotNull(
                            TxHistoryItemParam(
                                "amount",
                                "amount_123_bond"
                            ),
                            TxHistoryItemParam(
                                "accountId",
                                "accountId_123_bond"
                            ),
                            TxHistoryItemParam(
                                "extrinsicHash",
                                "extrinsicHash_123_bond"
                            ),
                            TxHistoryItemParam(
                                "type",
                                "type_123_bond"
                            )
                        )
                    ),
                    TxHistoryItem(
                        id = "id_123_slash",
                        blockHash = "blockNumber_123_slash",
                        module = "slash",
                        method = "",
                        timestamp = "timestamp_123_slash",
                        nestedData = emptyList(),
                        networkFee = "0",
                        success = true,
                        data = listOfNotNull(
                            TxHistoryItemParam(
                                "era",
                                "era_123_slash"
                            ),
                            TxHistoryItemParam(
                                "amount",
                                "amount_123_slash"
                            ),
                            TxHistoryItemParam(
                                "accountId",
                                "accountId_123_slash"
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
                    request = giantSquidRequestToMock
                )
            }.returns(giantSquidResponseToReturn)
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
                    request = giantSquidRequestToMock
                )
            }.wasInvoked(1)

            assertTrue { result == expectedResult }
        }

}