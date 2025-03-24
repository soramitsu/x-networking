package jp.co.soramitsu.xnetworking.lib.datasources.chainsconfig.api

import jp.co.soramitsu.xnetworking.lib.datasources.chainsconfig.api.models.ExternalApiDAOException
import jp.co.soramitsu.xnetworking.lib.datasources.chainsconfig.api.models.ExternalApiType
import jp.co.soramitsu.xnetworking.lib.datasources.chainsconfig.api.models.StakingOption
import jp.co.soramitsu.xnetworking.lib.engines.rest.api.models.RestClientException
import kotlin.coroutines.cancellation.CancellationException

abstract class ConfigDAO {

    @Throws(
        CancellationException::class,
        RestClientException::class,
        ExternalApiDAOException::class
    )
    abstract suspend fun historyType(chainId: String): ExternalApiType

    @Throws(
        CancellationException::class,
        RestClientException::class,
        ExternalApiDAOException::class
    )
    abstract suspend fun historyUrl(chainId: String): String

    @Throws(
        CancellationException::class,
        RestClientException::class,
        ExternalApiDAOException::class
    )
    abstract suspend fun stakingType(chainId: String): ExternalApiType

    @Throws(
        CancellationException::class,
        RestClientException::class,
        ExternalApiDAOException::class
    )
    abstract suspend fun stakingUrl(chainId: String): String

    @Throws(
        CancellationException::class,
        RestClientException::class
    )
    abstract suspend fun staking(chainId: String): StakingOption?

}