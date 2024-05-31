package jp.co.soramitsu.xnetworking.lib.datasources.blockexplorer.impl.domain.referralreward

import jp.co.soramitsu.xnetworking.lib.datasources.blockexplorer.api.models.ReferralReward
import jp.co.soramitsu.xnetworking.lib.datasources.chainsconfig.api.ConfigDAO
import jp.co.soramitsu.xnetworking.lib.datasources.chainsconfig.api.models.ExternalApiType
import jp.co.soramitsu.xnetworking.lib.datasources.blockexplorer.api.adapters.ReferralRewardFetcher

class ReferralRewardFetcherFacade(
    private val configDAO: ConfigDAO,
    private val fetchersMap: Map<ExternalApiType, ReferralRewardFetcher>
): ReferralRewardFetcher() {

    override suspend fun fetch(chainId: String, address: String): List<ReferralReward> {
        val fetcher = checkNotNull(
            fetchersMap[configDAO.stakingType(chainId)]
        ) { "Remote ReferralReward Loader could not have been found." }

        return fetcher.fetch(chainId, address)
    }

}