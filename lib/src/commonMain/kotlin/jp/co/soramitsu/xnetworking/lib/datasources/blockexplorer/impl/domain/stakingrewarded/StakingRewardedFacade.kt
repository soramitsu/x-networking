package jp.co.soramitsu.xnetworking.lib.datasources.blockexplorer.impl.domain.stakingrewarded

import jp.co.soramitsu.xnetworking.lib.datasources.blockexplorer.api.adapters.StakingRewardedFetcher
import jp.co.soramitsu.xnetworking.lib.datasources.blockexplorer.impl.domain.stakingrewarded.sora.StakingRewardedSoraFetcher
import jp.co.soramitsu.xnetworking.lib.datasources.chainsconfig.api.ConfigDAO
import jp.co.soramitsu.xnetworking.lib.datasources.chainsconfig.api.models.ExternalApiType
import jp.co.soramitsu.xnetworking.lib.datasources.txhistory.api.TxHistoryRepository
import jp.co.soramitsu.xnetworking.lib.engines.utils.CachingFactory

class StakingRewardedFacade(
    private val configDAO: ConfigDAO,
    private val txHistoryRepository: TxHistoryRepository,
) : StakingRewardedFetcher() {

    private data class Args(
        val externalApiType: ExternalApiType
    ) : CachingFactory.Args()

    private val cachingFactory = CachingFactory<Args, StakingRewardedBlockchainBasic> {
        if (externalApiType === ExternalApiType.Sora)
            return@CachingFactory StakingRewardedSoraFetcher(
                txHistoryRepository = txHistoryRepository,
            )

        error("No StakingRewardedFacade found")
    }

    override suspend fun fetch(
        chainId: String,
        address: String,
    ): List<String> {
        return cachingFactory.getOrCreate(
            args = Args(
                externalApiType = configDAO.historyType(chainId)
            )
        ).fetch(address, chainId)
    }
}

internal interface StakingRewardedBlockchainBasic {
    suspend fun fetch(
        address: String,
        chainId: String,
    ): List<String>
}
