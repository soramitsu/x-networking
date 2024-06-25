package jp.co.soramitsu.xnetworking.lib.datasources.chainsconfig.impl

import jp.co.soramitsu.xnetworking.lib.datasources.chainsconfig.api.data.ConfigParser
import jp.co.soramitsu.xnetworking.lib.datasources.chainsconfig.api.ConfigDAO
import jp.co.soramitsu.xnetworking.lib.datasources.chainsconfig.api.models.ExternalApiDAOException
import jp.co.soramitsu.xnetworking.lib.datasources.chainsconfig.api.models.ExternalApiType
import jp.co.soramitsu.xnetworking.lib.datasources.chainsconfig.api.models.StakingOption
import jp.co.soramitsu.xnetworking.lib.engines.utils.enumValueOfNullable
import jp.co.soramitsu.xnetworking.lib.engines.utils.arrayOrNull
import jp.co.soramitsu.xnetworking.lib.engines.utils.fieldOrNull
import jp.co.soramitsu.xnetworking.lib.engines.utils.objectOrNull

class SuperWalletConfigDAOImpl(
    private val configParser: ConfigParser
): ConfigDAO() {

    override suspend fun historyType(chainId: String): ExternalApiType {
        val historyType = configParser.getChainObjectById(chainId)
            .objectOrNull("externalApi")
            .objectOrNull("history")
            .fieldOrNull("type")

        return ExternalApiType.valueOf(historyType)
            ?: throw ExternalApiDAOException.NullType(chainId)
    }

    override suspend fun historyUrl(chainId: String): String {
        val historyUrl = configParser.getChainObjectById(chainId)
            .objectOrNull("externalApi")
            .objectOrNull("history")
            .fieldOrNull("url")

        return historyUrl ?: throw ExternalApiDAOException.NullUrl(chainId)
    }

    override suspend fun stakingType(chainId: String): ExternalApiType {
        val stakingType = configParser.getChainObjectById(chainId)
            .objectOrNull("externalApi")
            .objectOrNull("staking")
            .fieldOrNull("type")

        return ExternalApiType.valueOf(stakingType)
            ?: throw ExternalApiDAOException.NullType(chainId)
    }

    override suspend fun stakingUrl(chainId: String): String {
        val historyUrl = configParser.getChainObjectById(chainId)
            .objectOrNull("externalApi")
            .objectOrNull("staking")
            .fieldOrNull("url")

        return historyUrl ?: throw ExternalApiDAOException.NullUrl(chainId)
    }

    override suspend fun staking(chainId: String): StakingOption? {
        val staking = configParser.getChainObjectById(chainId)
            .fieldOrNull("staking")

        return enumValueOfNullable<StakingOption>(staking)
    }

}