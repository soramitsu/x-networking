package jp.co.soramitsu.xnetworking.lib.datasources.blockexplorer.impl.domain.apy.adapters.subquery

import jp.co.soramitsu.xnetworking.lib.datasources.chainsconfig.api.ConfigDAO
import jp.co.soramitsu.xnetworking.lib.datasources.chainsconfig.api.models.StakingOption
import jp.co.soramitsu.xnetworking.lib.datasources.blockexplorer.api.adapters.ApyFetcher
import jp.co.soramitsu.xnetworking.lib.datasources.blockexplorer.api.models.Apy
import jp.co.soramitsu.xnetworking.lib.engines.rest.api.RestClient

class SubQueryApyFetcher(
    private val configDAO: ConfigDAO,
    private val restClient: RestClient
): ApyFetcher() {

    override suspend fun fetch(
        chainId: String,
        selectedCandidates: List<String>?
    ): List<Apy> {
        require(
            selectedCandidates?.all { it.startsWith("0x") } ?: true
        ) { "Selected Collator Ids are not hex values." }

        check(
            configDAO.staking(chainId) === StakingOption.PARACHAIN
        ) { "Fetching of Apy from block explorer is only allowed in networks with paraChain type of staking" }

        val roundId = restClient.post(
            request = SubQueryLastRoundRequest(
                url = configDAO.stakingUrl(chainId)
            ),
        ).data.rounds.nodes.firstOrNull()
            ?.id?.toIntOrNull()

        val prevRoundId = roundId?.dec()

        val collatorsInfo = restClient.post(
            request = SubQueryApyRequest(
                url = configDAO.stakingUrl(chainId),
                collatorIds = selectedCandidates,
                roundId = prevRoundId
            )
        ).data.collatorRounds.nodes

        return collatorsInfo.mapNotNull {
            if (it.collatorId == null)
                return@mapNotNull null

            return@mapNotNull Apy(
                id = it.collatorId,
                value = it.apr
            )
        }
    }

}