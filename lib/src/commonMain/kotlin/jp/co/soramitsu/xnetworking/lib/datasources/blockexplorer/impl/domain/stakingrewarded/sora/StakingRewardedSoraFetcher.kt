package jp.co.soramitsu.xnetworking.lib.datasources.blockexplorer.impl.domain.stakingrewarded.sora

import jp.co.soramitsu.xnetworking.lib.datasources.blockexplorer.impl.domain.stakingrewarded.StakingRewardedBlockchainBasic
import jp.co.soramitsu.xnetworking.lib.datasources.txhistory.api.TxHistoryRepository

internal class StakingRewardedSoraFetcher(
    private val txHistoryRepository: TxHistoryRepository,
) : StakingRewardedBlockchainBasic {

    override suspend fun fetch(
        address: String,
        chainId: String,
    ): List<String> {
        val cache = txHistoryRepository.getTransactionHistoryCached(
            count = Int.MAX_VALUE,
            address = address,
            chainId = chainId,
        ).filter {
            it.module == "staking" && it.method == "Rewarded"
        }.mapNotNull {
            it.data?.find { param ->
                param.paramName == "amount"
            }
        }
        return cache.map {
            it.paramValue
        }
    }
}