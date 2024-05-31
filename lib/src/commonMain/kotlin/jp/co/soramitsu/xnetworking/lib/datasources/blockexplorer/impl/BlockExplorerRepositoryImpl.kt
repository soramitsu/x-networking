package jp.co.soramitsu.xnetworking.lib.datasources.blockexplorer.impl

import jp.co.soramitsu.xnetworking.lib.datasources.blockexplorer.api.models.AssetInfo
import jp.co.soramitsu.xnetworking.lib.datasources.blockexplorer.api.models.Fiat
import jp.co.soramitsu.xnetworking.lib.datasources.blockexplorer.api.models.ReferralReward
import jp.co.soramitsu.xnetworking.lib.datasources.blockexplorer.api.BlockExplorerRepository
import jp.co.soramitsu.xnetworking.lib.datasources.blockexplorer.api.adapters.ApyFetcher
import jp.co.soramitsu.xnetworking.lib.datasources.blockexplorer.api.adapters.AssetInfoFetcher
import jp.co.soramitsu.xnetworking.lib.datasources.blockexplorer.api.adapters.FiatFetcher
import jp.co.soramitsu.xnetworking.lib.datasources.blockexplorer.api.adapters.ReferralRewardFetcher
import jp.co.soramitsu.xnetworking.lib.datasources.blockexplorer.api.models.Unbonding
import jp.co.soramitsu.xnetworking.lib.datasources.blockexplorer.api.adapters.UnbondingFetcher
import jp.co.soramitsu.xnetworking.lib.datasources.blockexplorer.api.adapters.ValidatorsFetcher
import jp.co.soramitsu.xnetworking.lib.datasources.blockexplorer.api.models.Apy

class BlockExplorerRepositoryImpl(
    private val apyFetcher: ApyFetcher,
    private val assetInfoFetcher: AssetInfoFetcher,
    private val fiatFetcher: FiatFetcher,
    private val referralRewardFetcher: ReferralRewardFetcher,
    private val unbondingFetcher: UnbondingFetcher,
    private val validatorsFetcher: ValidatorsFetcher
): BlockExplorerRepository() {

    override suspend fun getApy(
        chainId: String,
        selectedCandidates: List<String>?
    ): List<Apy> {
        return apyFetcher.fetch(chainId, selectedCandidates)
    }

    override suspend fun getAssetsInfo(
        chainId: String,
        tokenIds: List<String>,
        timeStamp: Int
    ): List<AssetInfo> {
        return assetInfoFetcher.fetch(chainId, tokenIds, timeStamp)
    }

    override suspend fun getFiat(chainId: String): List<Fiat> {
        return fiatFetcher.fetch(chainId)
    }

    override suspend fun getReferralReward(
        chainId: String,
        address: String
    ): List<ReferralReward> {
        return referralRewardFetcher.fetch(chainId, address)
    }

    override suspend fun getUnbondingsList(
        chainId: String,
        delegatorAddress: String,
        collatorAddress: String
    ): List<Unbonding> {
        return unbondingFetcher.fetch(chainId, delegatorAddress, collatorAddress)
    }

    override suspend fun getValidatorsList(
        chainId: String,
        stashAccountAddress: String,
        historicalRange: List<String>
    ): List<String> {
        return validatorsFetcher.fetch(chainId, stashAccountAddress, historicalRange)
    }

}