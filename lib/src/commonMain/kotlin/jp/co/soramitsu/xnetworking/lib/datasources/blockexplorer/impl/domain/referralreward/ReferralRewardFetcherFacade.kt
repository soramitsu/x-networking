package jp.co.soramitsu.xnetworking.lib.datasources.blockexplorer.impl.domain.referralreward

import jp.co.soramitsu.xnetworking.lib.datasources.blockexplorer.api.adapters.ReferralRewardFetcher
import jp.co.soramitsu.xnetworking.lib.datasources.blockexplorer.api.models.ReferralReward
import jp.co.soramitsu.xnetworking.lib.datasources.blockexplorer.impl.domain.referralreward.sora.SoraReferralRewardsFetcher
import jp.co.soramitsu.xnetworking.lib.datasources.chainsconfig.api.ConfigDAO
import jp.co.soramitsu.xnetworking.lib.datasources.chainsconfig.api.models.ExternalApiType
import jp.co.soramitsu.xnetworking.lib.engines.apollo.api.ApolloClientStore
import jp.co.soramitsu.xnetworking.lib.engines.utils.CachingFactory

class ReferralRewardFetcherFacade(
    private val configDAO: ConfigDAO,
    private val apolloClientStore: ApolloClientStore
): ReferralRewardFetcher() {
    private data class Args(
        val externalApiType: ExternalApiType
    ): CachingFactory.Args()

    private val cachingFactory = CachingFactory<Args, ReferralRewardFetcher> {
        if (externalApiType === ExternalApiType.Sora)
            return@CachingFactory SoraReferralRewardsFetcher(
                apolloClientStore = apolloClientStore,
                configDAO = configDAO
            )

        error("Remote ReferralReward Loader could not have been found.")
    }

    override suspend fun fetch(chainId: String, address: String): List<ReferralReward> =
        cachingFactory.getOrCreate(
            args = Args(externalApiType = configDAO.historyType(chainId))
        ).fetch(chainId, address)

}