package jp.co.soramitsu.xnetworking.lib.datasources.blockexplorer.impl.domain.validators.adapters.subquery

import jp.co.soramitsu.xnetworking.lib.datasources.chainsconfig.api.ConfigDAO
import jp.co.soramitsu.xnetworking.lib.datasources.chainsconfig.api.models.StakingOption
import jp.co.soramitsu.xnetworking.lib.datasources.blockexplorer.api.adapters.ValidatorsFetcher
import jp.co.soramitsu.xnetworking.lib.engines.rest.api.RestClient

class SubQueryValidatorsFetcher(
    private val configDAO: ConfigDAO,
    private val restClient: RestClient
): ValidatorsFetcher() {

    override suspend fun fetch(
        chainId: String,
        stashAccountAddress: String,
        historicalRange: List<String>
    ): List<String> {
        require(
            historicalRange.isNotEmpty()
        ) { "HistoricalRange provided is empty." }

        check(
            configDAO.staking(chainId) === StakingOption.RELAYCHAIN
        ) { "Fetching of Validators from block explorer is only allowed in networks with relayChain type of staking" }

        val validatorsInfoList = restClient.post(
            request = SubQueryValidatorsRequest(
                url = configDAO.stakingUrl(chainId),
                accountAddress = stashAccountAddress,
                eraFrom = historicalRange.first(),
                eraTo = historicalRange.last()
            )
        ).data.query?.eraValidatorInfos?.nodes.orEmpty()

        return validatorsInfoList.map(
            transform = SubQueryValidatorsResponse.EraValidatorInfo.Nodes.Node::address
        ).distinct()
    }

}