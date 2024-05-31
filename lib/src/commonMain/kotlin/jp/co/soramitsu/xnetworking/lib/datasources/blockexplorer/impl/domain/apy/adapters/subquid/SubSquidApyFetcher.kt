package jp.co.soramitsu.xnetworking.lib.datasources.blockexplorer.impl.domain.apy.adapters.subquid

import jp.co.soramitsu.xnetworking.lib.datasources.chainsconfig.api.ConfigDAO
import jp.co.soramitsu.xnetworking.lib.datasources.chainsconfig.api.models.StakingOption
import jp.co.soramitsu.xnetworking.lib.datasources.blockexplorer.api.adapters.ApyFetcher
import jp.co.soramitsu.xnetworking.lib.datasources.blockexplorer.api.models.Apy
import jp.co.soramitsu.xnetworking.lib.engines.rest.api.RestClient

class SubSquidApyFetcher(
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

        val stakers = restClient.post(
            request = SubSquidApyRequest(
                url = configDAO.stakingUrl(chainId)
            )
        ).data.stakers

        return stakers.mapNotNull {
            if (it.stashId == null)
                return@mapNotNull null

            return@mapNotNull Apy(
                id = it.stashId,
                value = it.apr24h
            )
        }
    }

}