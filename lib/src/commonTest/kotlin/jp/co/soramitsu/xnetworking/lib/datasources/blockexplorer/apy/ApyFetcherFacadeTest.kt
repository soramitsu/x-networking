package jp.co.soramitsu.xnetworking.lib.datasources.blockexplorer.apy

import io.mockative.Mock
import io.mockative.classOf
import io.mockative.coEvery
import io.mockative.coVerify
import io.mockative.mock
import jp.co.soramitsu.xnetworking.lib.datasources.chainsconfig.api.ConfigDAO
import jp.co.soramitsu.xnetworking.lib.datasources.chainsconfig.api.models.ExternalApiDAOException
import jp.co.soramitsu.xnetworking.lib.datasources.chainsconfig.api.models.ExternalApiType
import jp.co.soramitsu.xnetworking.lib.datasources.blockexplorer.api.adapters.ApyFetcher
import jp.co.soramitsu.xnetworking.lib.datasources.blockexplorer.api.models.Apy
import jp.co.soramitsu.xnetworking.lib.datasources.blockexplorer.impl.domain.apy.ApyFetcherFacade
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertFailsWith

class ApyFetcherFacadeTest {

    private companion object {
        const val chainId = "sora"
        val selectedCandidates = emptyList<String>()
    }

    @Mock
    private val configDAO = mock(classOf<ConfigDAO>())

    @Mock
    private val apyFetcherMock = mock(classOf<ApyFetcher>())

    @Test
    fun `TEST apyFacade_fetch EXPECT ExternalApiDAOException_NullType BECAUSE staking type is null`() = runTest {
        // Test Data Start
        val facade: ApyFetcher =
            ApyFetcherFacade(
                configDAO = configDAO,
                fetchersMap = mapOf(
                    ExternalApiType.SORA to apyFetcherMock
                )
            )
        // Test Data End

        // Mocks Preparation Start
        coEvery {
            configDAO.stakingType(
                chainId = chainId
            )
        }.throws(ExternalApiDAOException.NullType(chainId))
        // Mocks Preparation End

        assertFailsWith<ExternalApiDAOException.NullType> {
            facade.fetch(
                chainId = chainId,
                selectedCandidates = selectedCandidates
            )
        }

        // Verification & Assertion
        coVerify {
            apyFetcherMock.fetch(
                chainId = chainId,
                selectedCandidates = selectedCandidates
            )
        }.wasNotInvoked()
    }

    @Test
    fun `TEST apyFacade_fetch EXPECT IllegalStateException BECAUSE fetcher not found`() = runTest {
        // Test Data Start
        val facade: ApyFetcher =
            ApyFetcherFacade(
                configDAO = configDAO,
                fetchersMap = mapOf(
                    ExternalApiType.SORA to apyFetcherMock
                )
            )
        // Test Data End

        // Mocks Preparation Start
        coEvery {
            configDAO.stakingType(
                chainId = chainId
            )
        }.returns(ExternalApiType.SUBQUERY)
        // Mocks Preparation End

        assertFailsWith<IllegalStateException> {
            facade.fetch(
                chainId = chainId,
                selectedCandidates = selectedCandidates
            )
        }

        // Verification & Assertion
        coVerify {
            apyFetcherMock.fetch(
                chainId = chainId,
                selectedCandidates = selectedCandidates
            )
        }.wasNotInvoked()
    }

    @Test
    fun `TEST apyFacade_fetch EXPECT success`() = runTest {
        // Test Data Start
        val facade: ApyFetcher =
            ApyFetcherFacade(
                configDAO = configDAO,
                fetchersMap = mapOf(
                    ExternalApiType.SORA to apyFetcherMock
                )
            )

        val apyToReturn = listOf(
            Apy("something", null)
        )
        // Test Data End

        // Mocks Preparation Start
        coEvery {
            configDAO.stakingType(
                chainId = chainId
            )
        }.returns(ExternalApiType.SORA)

        coEvery {
            apyFetcherMock.fetch(
                chainId = chainId,
                selectedCandidates = selectedCandidates
            )
        }.returns(apyToReturn)
        // Mocks Preparation End

        val result = facade.fetch(
            chainId = chainId,
            selectedCandidates = selectedCandidates
        )

        // Verification & Assertion
        coVerify {
            apyFetcherMock.fetch(
                chainId = chainId,
                selectedCandidates = selectedCandidates
            )
        }.wasInvoked(1)


        assertContentEquals(apyToReturn, result)
    }

}