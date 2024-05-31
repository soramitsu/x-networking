package jp.co.soramitsu.xnetworking.lib.datasources.blockexplorer.fiat

import io.mockative.Mock
import io.mockative.classOf
import io.mockative.coEvery
import io.mockative.coVerify
import io.mockative.mock
import jp.co.soramitsu.xnetworking.lib.datasources.chainsconfig.api.ConfigDAO
import jp.co.soramitsu.xnetworking.lib.datasources.chainsconfig.api.models.ExternalApiDAOException
import jp.co.soramitsu.xnetworking.lib.datasources.chainsconfig.api.models.ExternalApiType
import jp.co.soramitsu.xnetworking.lib.datasources.blockexplorer.api.adapters.FiatFetcher
import jp.co.soramitsu.xnetworking.lib.datasources.blockexplorer.api.models.Fiat
import jp.co.soramitsu.xnetworking.lib.datasources.blockexplorer.impl.domain.fiat.FiatFetcherFacade
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertFailsWith

class FiatFetcherFacadeTest {

    private companion object {
        const val chainId = "sora"
    }

    @Mock
    private val configDAO = mock(classOf<ConfigDAO>())

    @Mock
    private val fiatFetcherMock = mock(classOf<FiatFetcher>())

    @Test
    fun `TEST fiatFacade_fetch EXPECT ExternalApiDAOException_NullType BECAUSE externalApi_stakingType is null`() =
        runTest {
            // Test Data Start
            val facade: FiatFetcher =
                FiatFetcherFacade(
                    configDAO = configDAO,
                    fetchersMap = mapOf(
                        ExternalApiType.SORA to fiatFetcherMock
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
                    chainId = chainId
                )
            }

            // Verification & Assertion
            coVerify {
                fiatFetcherMock.fetch(
                    chainId = chainId
                )
            }.wasNotInvoked()
        }

    @Test
    fun `TEST fiatFacade_fetch EXPECT IllegalStateException BECAUSE fetcher not found`() =
        runTest {
            // Test Data Start
            val facade: FiatFetcher =
                FiatFetcherFacade(
                    configDAO = configDAO,
                    fetchersMap = mapOf(
                        ExternalApiType.SORA to fiatFetcherMock
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
                    chainId = chainId
                )
            }

            // Verification & Assertion
            coVerify {
                fiatFetcherMock.fetch(
                    chainId = chainId
                )
            }.wasNotInvoked()
        }

    @Test
    fun `TEST fiatFacade_fetch EXPECT success`() = runTest {
        // Test Data Start
        val facade: FiatFetcher =
            FiatFetcherFacade(
                configDAO = configDAO,
                fetchersMap = mapOf(
                    ExternalApiType.SORA to fiatFetcherMock
                )
            )

        val fiatListToReturn = listOf(
            Fiat(
                id = "123",
                priceUSD = "123"
            )
        )
        // Test Data End

        // Mocks Preparation Start
        coEvery {
            configDAO.stakingType(
                chainId = chainId
            )
        }.returns(ExternalApiType.SORA)

        coEvery {
            fiatFetcherMock.fetch(
                chainId = chainId
            )
        }.returns(fiatListToReturn)
        // Mocks Preparation End


        val result = facade.fetch(
            chainId = chainId
        )

        // Verification & Assertion
        coVerify {
            fiatFetcherMock.fetch(
                chainId = chainId
            )
        }.wasInvoked(1)

        assertContentEquals(
            fiatListToReturn,
            result
        )
    }
}