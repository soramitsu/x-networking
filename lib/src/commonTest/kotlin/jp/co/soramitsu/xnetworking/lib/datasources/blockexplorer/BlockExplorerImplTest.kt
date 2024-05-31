package jp.co.soramitsu.xnetworking.lib.datasources.blockexplorer

import io.mockative.Mock
import io.mockative.classOf
import io.mockative.coEvery
import io.mockative.coVerify
import io.mockative.mock
import jp.co.soramitsu.xnetworking.lib.datasources.blockexplorer.api.BlockExplorerRepository
import jp.co.soramitsu.xnetworking.lib.datasources.blockexplorer.api.adapters.ApyFetcher
import jp.co.soramitsu.xnetworking.lib.datasources.blockexplorer.api.adapters.AssetInfoFetcher
import jp.co.soramitsu.xnetworking.lib.datasources.blockexplorer.api.adapters.FiatFetcher
import jp.co.soramitsu.xnetworking.lib.datasources.blockexplorer.api.adapters.ReferralRewardFetcher
import jp.co.soramitsu.xnetworking.lib.datasources.blockexplorer.api.adapters.UnbondingFetcher
import jp.co.soramitsu.xnetworking.lib.datasources.blockexplorer.api.adapters.ValidatorsFetcher
import jp.co.soramitsu.xnetworking.lib.datasources.blockexplorer.impl.BlockExplorerRepositoryImpl
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

class BlockExplorerImplTest {

    @Mock
    private val apyFetcherMock = mock(classOf<ApyFetcher>())

    @Mock
    private val assetInfoFetcherMock = mock(classOf<AssetInfoFetcher>())

    @Mock
    private val fiatFetcherMock = mock(classOf<FiatFetcher>())

    @Mock
    private val referralRewardFetcherMock = mock(classOf<ReferralRewardFetcher>())

    @Mock
    private val unbondingFetcherMock = mock(classOf<UnbondingFetcher>())

    @Mock
    private val validatorsFetcherMock = mock(classOf<ValidatorsFetcher>())

    private val blockExplorerRepository: BlockExplorerRepository =
        BlockExplorerRepositoryImpl(
            apyFetcher = apyFetcherMock,
            assetInfoFetcher = assetInfoFetcherMock,
            fiatFetcher = fiatFetcherMock,
            referralRewardFetcher = referralRewardFetcherMock,
            unbondingFetcher = unbondingFetcherMock,
            validatorsFetcher = validatorsFetcherMock
        )

    @Test
    fun `TEST getApy EXPECT success`() = runTest {
        val chainId = "sora"
        val selectedCandidates = null

        coEvery {
            apyFetcherMock.fetch(
                chainId = chainId,
                selectedCandidates = selectedCandidates
            )
        }.returns(emptyList())

        blockExplorerRepository.getApy(
            chainId = chainId,
            selectedCandidates = selectedCandidates
        )

        coVerify {
            apyFetcherMock.fetch(
                chainId = chainId,
                selectedCandidates = selectedCandidates
            )
        }.wasInvoked(1)
    }

    @Test
    fun `TEST getAssetInfo EXPECT success`() = runTest {
        val chainId = "sora"
        val tokenIds = listOf<String>()
        val timeStamp = 0

        coEvery {
            assetInfoFetcherMock.fetch(
                chainId = chainId,
                tokenIds = tokenIds,
                timeStamp = timeStamp
            )
        }.returns(emptyList())

        blockExplorerRepository.getAssetsInfo(
            chainId = chainId,
            tokenIds = tokenIds,
            timeStamp = timeStamp
        )

        coVerify {
            assetInfoFetcherMock.fetch(
                chainId = chainId,
                tokenIds = tokenIds,
                timeStamp = timeStamp
            )
        }.wasInvoked(1)
    }

    @Test
    fun `TEST getFiat EXPECT success`() = runTest {
        val chainId = "sora"

        coEvery {
            fiatFetcherMock.fetch(
                chainId = chainId
            )
        }.returns(emptyList())

        blockExplorerRepository.getFiat(
            chainId = chainId
        )

        coVerify {
            fiatFetcherMock.fetch(
                chainId = chainId
            )
        }.wasInvoked(1)
    }

    @Test
    fun `TEST getReferralReward EXPECT success`() = runTest {
        val chainId = "sora"
        val address = "address"

        coEvery {
            referralRewardFetcherMock.fetch(
                chainId = chainId,
                address = address
            )
        }.returns(emptyList())

        blockExplorerRepository.getReferralReward(
            chainId = chainId,
            address = address
        )

        coVerify {
            referralRewardFetcherMock.fetch(
                chainId = chainId,
                address = address
            )
        }.wasInvoked(1)
    }

    @Test
    fun `TEST getUnbondingsList EXPECT success`() = runTest {
        val chainId = "sora"
        val delegatorAddress = ""
        val collatorAddress = ""

        coEvery {
            unbondingFetcherMock.fetch(
                chainId = chainId,
                delegatorAddress = delegatorAddress,
                collatorAddress = collatorAddress
            )
        }.returns(emptyList())

        blockExplorerRepository.getUnbondingsList(
            chainId = chainId,
            delegatorAddress = delegatorAddress,
            collatorAddress = collatorAddress
        )

        coVerify {
            unbondingFetcherMock.fetch(
                chainId = chainId,
                delegatorAddress = delegatorAddress,
                collatorAddress = collatorAddress
            )
        }.wasInvoked(1)
    }

    @Test
    fun `TEST getValidatorsList EXPECT success`() = runTest {
        val chainId = "sora"
        val stashAccountAddress = ""
        val historicalRange = emptyList<String>()

        coEvery {
            validatorsFetcherMock.fetch(
                chainId = chainId,
                stashAccountAddress = stashAccountAddress,
                historicalRange = historicalRange
            )
        }.returns(emptyList())

        blockExplorerRepository.getValidatorsList(
            chainId = chainId,
            stashAccountAddress = stashAccountAddress,
            historicalRange = historicalRange
        )

        coVerify {
            validatorsFetcherMock.fetch(
                chainId = chainId,
                stashAccountAddress = stashAccountAddress,
                historicalRange = historicalRange
            )
        }.wasInvoked(1)
    }

}