package jp.co.soramitsu.xnetworking.lib.datasources.blockexplorer.impl.domain.validators

import jp.co.soramitsu.xnetworking.lib.datasources.chainsconfig.api.ConfigDAO
import jp.co.soramitsu.xnetworking.lib.datasources.chainsconfig.api.models.ExternalApiType
import jp.co.soramitsu.xnetworking.lib.datasources.blockexplorer.api.adapters.ValidatorsFetcher
import jp.co.soramitsu.xnetworking.lib.engines.utils.CachingFactory
import jp.co.soramitsu.xnetworking.lib.datasources.blockexplorer.impl.domain.validators.adapters.sora.SoraValidatorsFetcher
import jp.co.soramitsu.xnetworking.lib.datasources.blockexplorer.impl.domain.validators.adapters.subquery.SubQueryValidatorsFetcher
import jp.co.soramitsu.xnetworking.lib.datasources.blockexplorer.impl.domain.validators.adapters.subsquid.SubSquidValidatorsFetcher
import jp.co.soramitsu.xnetworking.lib.engines.rest.api.RestClient

class ValidatorsFetcherFacade(
    private val configDAO: ConfigDAO,
    private val restClient: RestClient
): ValidatorsFetcher() {
    private data class Args(
        val externalApiType: ExternalApiType
    ): CachingFactory.Args()

    private val cachingFactory = CachingFactory<Args, ValidatorsFetcher> {
        if (externalApiType === ExternalApiType.Sora) {
            return@CachingFactory SoraValidatorsFetcher(configDAO, restClient)
        }

        if (externalApiType === ExternalApiType.SubSquid) {
            return@CachingFactory SubSquidValidatorsFetcher()
        }

        if (externalApiType === ExternalApiType.SubQuery) {
            return@CachingFactory SubQueryValidatorsFetcher(configDAO, restClient)
        }

        error("Remote Unbondings Loader could not have been found.")
    }

    override suspend fun fetch(
        chainId: String,
        stashAccountAddress: String,
        historicalRange: List<String>
    ): List<String> = cachingFactory.getOrCreate(
        args = Args(externalApiType = configDAO.stakingType(chainId))
    ).fetch(chainId, stashAccountAddress, historicalRange)

}