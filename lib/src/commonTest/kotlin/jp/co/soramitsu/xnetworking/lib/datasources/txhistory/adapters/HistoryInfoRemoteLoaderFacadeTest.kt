package jp.co.soramitsu.xnetworking.lib.datasources.txhistory.adapters

import io.mockative.Mock
import io.mockative.classOf
import io.mockative.coEvery
import io.mockative.coVerify
import io.mockative.mock
import jp.co.soramitsu.xnetworking.lib.datasources.chainsconfig.api.ConfigDAO
import jp.co.soramitsu.xnetworking.lib.datasources.chainsconfig.api.models.ExternalApiDAOException
import jp.co.soramitsu.xnetworking.lib.datasources.chainsconfig.api.models.ExternalApiType
import jp.co.soramitsu.xnetworking.lib.datasources.txhistory.api.models.ChainInfo
import jp.co.soramitsu.xnetworking.lib.datasources.txhistory.api.adapters.HistoryInfoRemoteLoader
import jp.co.soramitsu.xnetworking.lib.datasources.txhistory.api.models.TxFilter
import jp.co.soramitsu.xnetworking.lib.datasources.txhistory.api.models.TxHistoryInfo
import jp.co.soramitsu.xnetworking.lib.datasources.txhistory.impl.domain.adapters.HistoryInfoRemoteLoaderFacade
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class HistoryInfoRemoteLoaderFacadeTest {

    private companion object {
        const val chainId = "sora"

        val chainInfo = ChainInfo.Simple(
            chainId = chainId
        )

        const val cursor = ""
        const val pageCount = 0
        const val signAddress = ""

        val filters = emptySet<TxFilter>()
    }

    @Mock
    private val configDAO = mock(classOf<ConfigDAO>())

    @Mock
    private val historyInfoRemoteLoader = mock(classOf<HistoryInfoRemoteLoader>())

    @Test
    fun `TEST apyFacade_fetch EXPECT ExternalApiDAOException_NullType BECAUSE staking type is null`() = runTest {
        // Test Data Start
        val facade: HistoryInfoRemoteLoader =
            HistoryInfoRemoteLoaderFacade(
                configDAO = configDAO,
                loaders = mapOf(
                    ExternalApiType.SORA to historyInfoRemoteLoader
                )
            )
        // Test Data End

        // Mocks Preparation Start
        coEvery {
            configDAO.historyType(
                chainId = chainId
            )
        }.throws(ExternalApiDAOException.NullType(chainId))
        // Mocks Preparation End

        assertFailsWith<ExternalApiDAOException.NullType> {
            facade.loadHistoryInfo(
                pageCount = pageCount,
                cursor = cursor,
                signAddress = signAddress,
                chainInfo = chainInfo,
                filters = filters
            )
        }

        // Verification & Assertion
        coVerify {
            historyInfoRemoteLoader.loadHistoryInfo(
                pageCount = pageCount,
                cursor = cursor,
                signAddress = signAddress,
                chainInfo = chainInfo,
                filters = filters
            )
        }.wasNotInvoked()
    }

    @Test
    fun `TEST apyFacade_fetch EXPECT IllegalStateException BECAUSE fetcher not found`() = runTest {
        // Test Data Start
        val facade: HistoryInfoRemoteLoader =
            HistoryInfoRemoteLoaderFacade(
                configDAO = configDAO,
                loaders = mapOf(
                    ExternalApiType.SORA to historyInfoRemoteLoader
                )
            )
        // Test Data End

        // Mocks Preparation Start
        coEvery {
            configDAO.historyType(
                chainId = chainId
            )
        }.returns(ExternalApiType.SUBQUERY)
        // Mocks Preparation End

        assertFailsWith<IllegalStateException> {
            facade.loadHistoryInfo(
                pageCount = pageCount,
                cursor = cursor,
                signAddress = signAddress,
                chainInfo = chainInfo,
                filters = filters
            )
        }

        // Verification & Assertion
        coVerify {
            historyInfoRemoteLoader.loadHistoryInfo(
                pageCount = pageCount,
                cursor = cursor,
                signAddress = signAddress,
                chainInfo = chainInfo,
                filters = filters
            )
        }.wasNotInvoked()
    }

    @Test
    fun `TEST apyFacade_fetch EXPECT success`() = runTest {
        // Test Data Start
        val facade: HistoryInfoRemoteLoader =
            HistoryInfoRemoteLoaderFacade(
                configDAO = configDAO,
                loaders = mapOf(
                    ExternalApiType.SORA to historyInfoRemoteLoader
                )
            )

        val txHistoryInfo = TxHistoryInfo(
            endCursor = null,
            endReached = true,
            items = emptyList()
        )
        // Test Data End

        // Mocks Preparation Start
        coEvery {
            configDAO.historyType(
                chainId = chainId
            )
        }.returns(ExternalApiType.SORA)

        coEvery {
            historyInfoRemoteLoader.loadHistoryInfo(
                pageCount = pageCount,
                cursor = cursor,
                signAddress = signAddress,
                chainInfo = chainInfo,
                filters = filters
            )
        }.returns(txHistoryInfo)
        // Mocks Preparation End

        val result = facade.loadHistoryInfo(
            pageCount = pageCount,
            cursor = cursor,
            signAddress = signAddress,
            chainInfo = chainInfo,
            filters = filters
        )

        // Verification & Assertion
        coVerify {
            historyInfoRemoteLoader.loadHistoryInfo(
                pageCount = pageCount,
                cursor = cursor,
                signAddress = signAddress,
                chainInfo = chainInfo,
                filters = filters
            )
        }.wasInvoked(1)

        assertTrue { result == txHistoryInfo }
    }
}