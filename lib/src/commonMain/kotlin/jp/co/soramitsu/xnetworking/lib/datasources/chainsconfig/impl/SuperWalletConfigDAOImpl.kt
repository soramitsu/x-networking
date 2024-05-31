package jp.co.soramitsu.xnetworking.lib.datasources.chainsconfig.impl

import jp.co.soramitsu.xnetworking.lib.datasources.chainsconfig.api.data.ConfigFetcher
import jp.co.soramitsu.xnetworking.lib.datasources.chainsconfig.api.ConfigDAO
import jp.co.soramitsu.xnetworking.lib.datasources.chainsconfig.api.models.ExternalApiDAOException
import jp.co.soramitsu.xnetworking.lib.datasources.chainsconfig.api.models.ExternalApiType
import jp.co.soramitsu.xnetworking.lib.datasources.chainsconfig.api.models.StakingOption
import jp.co.soramitsu.xnetworking.lib.engines.utils.enumValueOfNullable
import jp.co.soramitsu.xnetworking.lib.engines.utils.arrayOrNull
import jp.co.soramitsu.xnetworking.lib.engines.utils.fieldOrNull
import jp.co.soramitsu.xnetworking.lib.engines.utils.objectOrNull

class SuperWalletConfigDAOImpl(
    private val configFetcher: ConfigFetcher
): ConfigDAO() {

    override suspend fun historyType(chainId: String): ExternalApiType {
        val historyType = configFetcher.fetch(chainId)
            .objectOrNull("externalApi")
            .objectOrNull("history")
            .fieldOrNull("type")

        return enumValueOfNullable<ExternalApiType>(historyType)
            ?: throw ExternalApiDAOException.NullType(chainId)
    }

    override suspend fun historyUrl(chainId: String): String {
        val historyUrl = configFetcher.fetch(chainId)
            .objectOrNull("externalApi")
            .objectOrNull("history")
            .fieldOrNull("url")

        return historyUrl ?: throw ExternalApiDAOException.NullUrl(chainId)
    }

    override suspend fun stakingType(chainId: String): ExternalApiType {
        val stakingType = configFetcher.fetch(chainId)
            .objectOrNull("externalApi")
            .objectOrNull("staking")
            .fieldOrNull("type")

        return enumValueOfNullable<ExternalApiType>(stakingType)
            ?: throw ExternalApiDAOException.NullType(chainId)
    }

    override suspend fun stakingUrl(chainId: String): String {
        val historyUrl = configFetcher.fetch(chainId)
            .objectOrNull("externalApi")
            .objectOrNull("staking")
            .fieldOrNull("url")

        return historyUrl ?: throw ExternalApiDAOException.NullUrl(chainId)
    }

    override suspend fun staking(chainId: String): StakingOption? {
        val staking = configFetcher.fetch(chainId)
            .objectOrNull("tokens")
            .arrayOrNull("tokens")
            ?.firstOrNull { it.fieldOrNull("isUtility") == "true" }
            .objectOrNull("tokenProperties")

        return enumValueOfNullable<StakingOption>(staking.fieldOrNull("staking"))
    }

}