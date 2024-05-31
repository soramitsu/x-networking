package jp.co.soramitsu.xnetworking.lib.datasources.blockexplorer.impl.domain.unbonding.adapters.subquery

import jp.co.soramitsu.xnetworking.lib.datasources.chainsconfig.api.ConfigDAO
import jp.co.soramitsu.xnetworking.lib.datasources.chainsconfig.api.models.StakingOption
import jp.co.soramitsu.xnetworking.lib.datasources.blockexplorer.api.adapters.UnbondingFetcher
import jp.co.soramitsu.xnetworking.lib.datasources.blockexplorer.api.models.Unbonding
import jp.co.soramitsu.xnetworking.lib.engines.rest.api.RestClient

class SubQueryUnbondingFetcher(
    private val configDAO: ConfigDAO,
    private val restClient: RestClient
): UnbondingFetcher() {

    override suspend fun fetch(
        chainId: String,
        delegatorAddress: String,
        collatorAddress: String
    ): List<Unbonding> {
        require(
            delegatorAddress.startsWith("0x")
        ) { "DelegatorAddress is not hex value address." }

        require(
            collatorAddress.startsWith("0x")
        ) { "CollatorAddress is not hex value address." }

        check(
            configDAO.staking(chainId) === StakingOption.PARACHAIN
        ) { "Fetching of Unbondings from block explorer is only allowed in networks with paraChain type of staking" }

        val nodes = restClient.post(
            request = SubQueryUnbondingRequest(
                url = configDAO.stakingUrl(chainId),
                delegatorAddress = delegatorAddress,
                collatorAddress = collatorAddress
            )
        ).data.delegatorHistoryElements.nodes

        return nodes.map { historyElement ->
            Unbonding(
                amount = historyElement.amount ?: "0",
                timestamp = historyElement.timestamp ?: "0",
                type = Unbonding.DelegationAction.REWARD
            )
        }
    }

}