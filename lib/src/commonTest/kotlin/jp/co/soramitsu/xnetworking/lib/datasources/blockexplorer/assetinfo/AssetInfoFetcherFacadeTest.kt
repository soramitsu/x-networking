package jp.co.soramitsu.xnetworking.lib.datasources.blockexplorer.assetinfo

import io.mockative.Mock
import io.mockative.classOf
import io.mockative.coEvery
import io.mockative.coVerify
import io.mockative.mock
import jp.co.soramitsu.xnetworking.lib.datasources.chainsconfig.api.ConfigDAO
import jp.co.soramitsu.xnetworking.lib.datasources.chainsconfig.api.models.ExternalApiDAOException
import jp.co.soramitsu.xnetworking.lib.datasources.chainsconfig.api.models.ExternalApiType
import jp.co.soramitsu.xnetworking.lib.datasources.chainsconfig.api.models.StakingOption
import jp.co.soramitsu.xnetworking.lib.datasources.blockexplorer.api.adapters.AssetInfoFetcher
import jp.co.soramitsu.xnetworking.lib.datasources.blockexplorer.api.models.AssetInfo
import jp.co.soramitsu.xnetworking.lib.datasources.blockexplorer.impl.domain.assetinfo.AssetInfoFetcherFacade
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertFailsWith

class AssetInfoFetcherFacadeTest {

    private companion object {
        const val chainId = "sora"
        val tokenIds = listOf<String>()
        const val timeStamp = 0
    }

    @Mock
    private val configDAO = mock(classOf<ConfigDAO>())

    @Mock
    private val assetInfoFetcherMock = mock(classOf<AssetInfoFetcher>())

    @Test
    fun `TEST assetInfoFacade_fetch EXPECT ExternalApiDAOException_NullType BECAUSE staking type is null`() =
        runTest {
            // Test Data Start
            val facade: AssetInfoFetcher =
                AssetInfoFetcherFacade(
                    configDAO = configDAO,
                    fetchersMap = mapOf(
                        ExternalApiType.SORA to assetInfoFetcherMock
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
                    tokenIds = tokenIds,
                    timeStamp = timeStamp
                )
            }

            // Verification & Assertion
            coVerify {
                assetInfoFetcherMock.fetch(
                    chainId = chainId,
                    tokenIds = tokenIds,
                    timeStamp = timeStamp
                )
            }.wasNotInvoked()
        }

    @Test
    fun `TEST assetInfoFacade_fetch EXPECT IllegalStateException BECAUSE fetcher not found`() =
        runTest {
            // Test Data Start
            val facade: AssetInfoFetcher =
                AssetInfoFetcherFacade(
                    configDAO = configDAO,
                    fetchersMap = mapOf(
                        ExternalApiType.SORA to assetInfoFetcherMock
                    )
                )
            // Test Data End

            // Mocks Preparation Start
            coEvery {
                configDAO.stakingType(
                    chainId = chainId
                )
            }.returns(ExternalApiType.SUBQUERY)

            coEvery {
                configDAO.staking(
                    chainId = chainId
                )
            }.returns(StakingOption.PARACHAIN)
            // Mocks Preparation End

            assertFailsWith<IllegalStateException> {
                facade.fetch(
                    chainId = chainId,
                    tokenIds = tokenIds,
                    timeStamp = timeStamp
                )
            }

            // Verification & Assertion
            coVerify {
                assetInfoFetcherMock.fetch(
                    chainId = chainId,
                    tokenIds = tokenIds,
                    timeStamp = timeStamp
                )
            }.wasNotInvoked()
        }

    @Test
    fun `TEST assetInfoFacade_fetch EXPECT success`() = runTest {
        // Test Data Start
        val facade: AssetInfoFetcher =
            AssetInfoFetcherFacade(
                configDAO = configDAO,
                fetchersMap = mapOf(
                    ExternalApiType.SORA to assetInfoFetcherMock
                )
            )

        val assetInfoListToReturn =
            listOf(
                AssetInfo(
                    id = "123",
                    liquidity = "123",
                    previousPrice = null
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
            configDAO.staking(
                chainId = chainId
            )
        }.returns(StakingOption.PARACHAIN)

        coEvery {
            assetInfoFetcherMock.fetch(
                chainId = chainId,
                tokenIds = tokenIds,
                timeStamp = timeStamp
            )
        }.returns(assetInfoListToReturn)
        // Mocks Preparation End

        val result = facade.fetch(
            chainId = chainId,
            tokenIds = tokenIds,
            timeStamp = timeStamp
        )

        // Verification & Assertion
        coVerify {
            assetInfoFetcherMock.fetch(
                chainId = chainId,
                tokenIds = tokenIds,
                timeStamp = timeStamp
            )
        }.wasInvoked(1)

        assertContentEquals(
            assetInfoListToReturn,
            result
        )
    }
}