package jp.co.soramitsu.xnetworking.lib.datasources.blockexplorer.validators

import io.mockative.Mock
import io.mockative.classOf
import io.mockative.coEvery
import io.mockative.coVerify
import io.mockative.mock
import jp.co.soramitsu.xnetworking.lib.datasources.chainsconfig.api.ConfigDAO
import jp.co.soramitsu.xnetworking.lib.datasources.chainsconfig.api.models.ExternalApiDAOException
import jp.co.soramitsu.xnetworking.lib.datasources.chainsconfig.api.models.ExternalApiType
import jp.co.soramitsu.xnetworking.lib.datasources.blockexplorer.api.adapters.ValidatorsFetcher
import jp.co.soramitsu.xnetworking.lib.datasources.blockexplorer.impl.domain.validators.ValidatorsFetcherFacade
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertFailsWith

class ValidatorsFetcherFacadeTest {

    private companion object {
        const val chainId = "sora"
        const val stashAccountAddress = ""

        val historicalRange = emptyList<String>()
    }

    @Mock
    private val configDAO = mock(classOf<ConfigDAO>())

    @Mock
    private val validatorsFetcherMock = mock(classOf<ValidatorsFetcher>())

    @Test
    fun `TEST validatorsFacade_fetch EXPECT ExternalApiDAOException_NullType BECAUSE externalApi_stakingType is null`() =
        runTest {
            // Test Data Start
            val facade: ValidatorsFetcher =
                ValidatorsFetcherFacade(
                    configDAO = configDAO,
                    fetchersMap = mapOf(
                        ExternalApiType.SORA to validatorsFetcherMock
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
                    stashAccountAddress = stashAccountAddress,
                    historicalRange = historicalRange
                )
            }

            // Verification & Assertion
            coVerify {
                validatorsFetcherMock.fetch(
                    chainId = chainId,
                    stashAccountAddress = stashAccountAddress,
                    historicalRange = historicalRange
                )
            }.wasNotInvoked()
        }

    @Test
    fun `TEST validatorsFacade_fetch EXPECT IllegalStateException BECAUSE fetcher not found`() =
        runTest {
            // Test Data Start
            val facade: ValidatorsFetcher =
                ValidatorsFetcherFacade(
                    configDAO = configDAO,
                    fetchersMap = mapOf(
                        ExternalApiType.SORA to validatorsFetcherMock
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
                    stashAccountAddress = stashAccountAddress,
                    historicalRange = historicalRange
                )
            }

            // Verification & Assertion
            coVerify {
                validatorsFetcherMock.fetch(
                    chainId = chainId,
                    stashAccountAddress = stashAccountAddress,
                    historicalRange = historicalRange
                )
            }.wasNotInvoked()
        }

    @Test
    fun `TEST validatorsFacade_fetch EXPECT success`() = runTest {
        // Test Data Start
        val facade: ValidatorsFetcher =
            ValidatorsFetcherFacade(
                configDAO = configDAO,
                fetchersMap = mapOf(
                    ExternalApiType.SORA to validatorsFetcherMock
                )
            )

        val validatorsListToReturn = listOf("something")
        // Test Data End

        // Mocks Preparation Start
        coEvery {
            configDAO.stakingType(
                chainId = chainId
            )
        }.returns(ExternalApiType.SORA)

        coEvery {
            validatorsFetcherMock.fetch(
                chainId = chainId,
                stashAccountAddress = stashAccountAddress,
                historicalRange = historicalRange
            )
        }.returns(validatorsListToReturn)
        // Mocks Preparation End


        val result = facade.fetch(
            chainId = chainId,
            stashAccountAddress = stashAccountAddress,
            historicalRange = historicalRange
        )

        // Verification & Assertion
        coVerify {
            validatorsFetcherMock.fetch(
                chainId = chainId,
                stashAccountAddress = stashAccountAddress,
                historicalRange = historicalRange
            )
        }.wasInvoked(1)

        assertContentEquals(
            validatorsListToReturn,
            result
        )
    }
}