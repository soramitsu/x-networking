package jp.co.soramitsu.xnetworking.lib.datasources.blockexplorer.referralreward

import io.mockative.Mock
import io.mockative.classOf
import io.mockative.coEvery
import io.mockative.coVerify
import io.mockative.mock
import jp.co.soramitsu.xnetworking.lib.datasources.chainsconfig.api.ConfigDAO
import jp.co.soramitsu.xnetworking.lib.datasources.chainsconfig.api.models.ExternalApiDAOException
import jp.co.soramitsu.xnetworking.lib.datasources.chainsconfig.api.models.ExternalApiType
import jp.co.soramitsu.xnetworking.lib.datasources.blockexplorer.api.adapters.ReferralRewardFetcher
import jp.co.soramitsu.xnetworking.lib.datasources.blockexplorer.api.models.ReferralReward
import jp.co.soramitsu.xnetworking.lib.datasources.blockexplorer.impl.domain.referralreward.ReferralRewardFetcherFacade
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertFailsWith

class ReferralRewardFetcherFacadeTest {

    private companion object {
        const val chainId = "sora"
        const val address = ""
    }

    @Mock
    private val configDAO = mock(classOf<ConfigDAO>())

    @Mock
    private val referralRewardFetcherMock = mock(classOf<ReferralRewardFetcher>())

    @Test
    fun `TEST referralRewardFacade_fetch EXPECT ExternalApiDAOException_NullType BECAUSE externalApi_stakingType is null`() =
        runTest {
            // Test Data Start
            val facade: ReferralRewardFetcher =
                ReferralRewardFetcherFacade(
                    configDAO = configDAO,
                    fetchersMap = mapOf(
                        ExternalApiType.SORA to referralRewardFetcherMock
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
                    address = address
                )
            }

            // Verification & Assertion
            coVerify {
                referralRewardFetcherMock.fetch(
                    chainId = chainId,
                    address = address
                )
            }.wasNotInvoked()
        }

    @Test
    fun `TEST referralRewardFacade_fetch EXPECT IllegalStateException BECAUSE fetcher not found`() =
        runTest {
            // Test Data Start
            val facade: ReferralRewardFetcher =
                ReferralRewardFetcherFacade(
                    configDAO = configDAO,
                    fetchersMap = mapOf(
                        ExternalApiType.SORA to referralRewardFetcherMock
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
                    address = address
                )
            }

            // Verification & Assertion
            coVerify {
                referralRewardFetcherMock.fetch(
                    chainId = chainId,
                    address = address
                )
            }.wasNotInvoked()
        }

    @Test
    fun `TEST referralRewardFacade_fetch EXPECT success`() = runTest {
        // Test Data Start
        val facade: ReferralRewardFetcher =
            ReferralRewardFetcherFacade(
                configDAO = configDAO,
                fetchersMap = mapOf(
                    ExternalApiType.SORA to referralRewardFetcherMock
                )
            )

        val referralRewardListToReturn = listOf(
            ReferralReward(
                referral = "referral_123",
                amount = "123"
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
            referralRewardFetcherMock.fetch(
                chainId = chainId,
                address = address
            )
        }.returns(referralRewardListToReturn)
        // Mocks Preparation End


        val result = facade.fetch(
            chainId = chainId,
            address = address
        )

        // Verification & Assertion
        coVerify {
            referralRewardFetcherMock.fetch(
                chainId = chainId,
                address = address
            )
        }.wasInvoked(1)

        assertContentEquals(
            referralRewardListToReturn,
            result
        )
    }

}