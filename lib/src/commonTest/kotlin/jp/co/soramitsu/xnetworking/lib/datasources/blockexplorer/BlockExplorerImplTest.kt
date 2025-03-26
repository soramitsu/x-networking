package jp.co.soramitsu.xnetworking.lib.datasources.blockexplorer

import jp.co.soramitsu.xnetworking.lib.datasources.blockexplorer.api.BlockExplorerRepository
import jp.co.soramitsu.xnetworking.lib.datasources.blockexplorer.api.adapters.ApyFetcher
import jp.co.soramitsu.xnetworking.lib.datasources.blockexplorer.api.adapters.AssetInfoFetcher
import jp.co.soramitsu.xnetworking.lib.datasources.blockexplorer.api.adapters.FiatFetcher
import jp.co.soramitsu.xnetworking.lib.datasources.blockexplorer.api.adapters.ReferralRewardFetcher
import jp.co.soramitsu.xnetworking.lib.datasources.blockexplorer.api.adapters.StakingRewardedFetcher
import jp.co.soramitsu.xnetworking.lib.datasources.blockexplorer.api.adapters.UnbondingFetcher
import jp.co.soramitsu.xnetworking.lib.datasources.blockexplorer.api.adapters.ValidatorsFetcher
import jp.co.soramitsu.xnetworking.lib.datasources.blockexplorer.api.models.Apy
import jp.co.soramitsu.xnetworking.lib.datasources.blockexplorer.api.models.AssetInfo
import jp.co.soramitsu.xnetworking.lib.datasources.blockexplorer.api.models.Fiat
import jp.co.soramitsu.xnetworking.lib.datasources.blockexplorer.api.models.ReferralReward
import jp.co.soramitsu.xnetworking.lib.datasources.blockexplorer.api.models.Unbonding
import jp.co.soramitsu.xnetworking.lib.datasources.blockexplorer.impl.BlockExplorerRepositoryImpl
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

private class FakeApyFetcher : ApyFetcher() {
    override suspend fun fetch(chainId: String, selectedCandidates: List<String>?): List<Apy> {
        return emptyList()
    }
}

private class FakeAssetInfoFetcher : AssetInfoFetcher() {
    override suspend fun fetch(
        chainId: String,
        tokenIds: List<String>,
        timeStamp: Int
    ): List<AssetInfo> {
        return emptyList()
    }
}

private class FakeFiatFetcher : FiatFetcher() {
    override suspend fun fetch(chainId: String): List<Fiat> {
        return emptyList()
    }
}

private class FakeReferralRewardFetcher : ReferralRewardFetcher() {
    override suspend fun fetch(
        chainId: String,
        address: String
    ): List<ReferralReward> {
        return emptyList()
    }
}

private class FakeUnbondingFetcher : UnbondingFetcher() {
    override suspend fun fetch(
        chainId: String,
        delegatorAddress: String,
        collatorAddress: String
    ): List<Unbonding> {
        return emptyList()
    }
}

private class FakeValidatorsFetcher : ValidatorsFetcher() {
    override suspend fun fetch(
        chainId: String,
        stashAccountAddress: String,
        historicalRange: List<String>
    ): List<String> {
        return emptyList()
    }
}

private class FakeStakingRewardedFetcher : StakingRewardedFetcher() {
    override suspend fun fetch(
        chainId: String,
        address: String
    ): List<String> {
        return emptyList()
    }
}

class BlockExplorerImplTest {

    private val apyFetcherMock = FakeApyFetcher()

    private val assetInfoFetcherMock = FakeAssetInfoFetcher()

    private val fiatFetcherMock = FakeFiatFetcher()

    private val referralRewardFetcherMock = FakeReferralRewardFetcher()

    private val unbondingFetcherMock = FakeUnbondingFetcher()

    private val validatorsFetcherMock = FakeValidatorsFetcher()

    private val stakingRewarded = FakeStakingRewardedFetcher()

    private val blockExplorerRepository: BlockExplorerRepository =
        BlockExplorerRepositoryImpl(
            apyFetcher = apyFetcherMock,
            assetInfoFetcher = assetInfoFetcherMock,
            fiatFetcher = fiatFetcherMock,
            referralRewardFetcher = referralRewardFetcherMock,
            unbondingFetcher = unbondingFetcherMock,
            validatorsFetcher = validatorsFetcherMock,
            stakingRewarded = stakingRewarded,
        )

    @Test
    fun `TEST getApy EXPECT success`() = runTest {
        val chainId = "sora"
        val selectedCandidates = null

        blockExplorerRepository.getApy(
            chainId = chainId,
            selectedCandidates = selectedCandidates
        )

//        coVerify {
//            apyFetcherMock.fetch(
//                chainId = chainId,
//                selectedCandidates = selectedCandidates
//            )
//        }.wasInvoked(1)
    }

    @Test
    fun `TEST getAssetInfo EXPECT success`() = runTest {
        val chainId = "sora"
        val tokenIds = listOf<String>()
        val timeStamp = 0

        blockExplorerRepository.getAssetsInfo(
            chainId = chainId,
            tokenIds = tokenIds,
            timeStamp = timeStamp
        )

//        coVerify {
//            assetInfoFetcherMock.fetch(
//                chainId = chainId,
//                tokenIds = tokenIds,
//                timeStamp = timeStamp
//            )
//        }.wasInvoked(1)
    }

    @Test
    fun `TEST getFiat EXPECT success`() = runTest {
        val chainId = "sora"

        blockExplorerRepository.getFiat(
            chainId = chainId
        )

//        coVerify {
//            fiatFetcherMock.fetch(
//                chainId = chainId
//            )
//        }.wasInvoked(1)
    }

    @Test
    fun `TEST getReferralReward EXPECT success`() = runTest {
        val chainId = "sora"
        val address = "address"

        blockExplorerRepository.getReferralReward(
            chainId = chainId,
            address = address
        )

//        coVerify {
//            referralRewardFetcherMock.fetch(
//                chainId = chainId,
//                address = address
//            )
//        }.wasInvoked(1)
    }

    @Test
    fun `TEST getUnbondingsList EXPECT success`() = runTest {
        val chainId = "sora"
        val delegatorAddress = ""
        val collatorAddress = ""

        blockExplorerRepository.getUnbondingsList(
            chainId = chainId,
            delegatorAddress = delegatorAddress,
            collatorAddress = collatorAddress
        )

//        coVerify {
//            unbondingFetcherMock.fetch(
//                chainId = chainId,
//                delegatorAddress = delegatorAddress,
//                collatorAddress = collatorAddress
//            )
//        }.wasInvoked(1)
    }

    @Test
    fun `TEST getValidatorsList EXPECT success`() = runTest {
        val chainId = "sora"
        val stashAccountAddress = ""
        val historicalRange = emptyList<String>()

        blockExplorerRepository.getValidatorsList(
            chainId = chainId,
            stashAccountAddress = stashAccountAddress,
            historicalRange = historicalRange
        )

//        coVerify {
//            validatorsFetcherMock.fetch(
//                chainId = chainId,
//                stashAccountAddress = stashAccountAddress,
//                historicalRange = historicalRange
//            )
//        }.wasInvoked(1)
    }
}