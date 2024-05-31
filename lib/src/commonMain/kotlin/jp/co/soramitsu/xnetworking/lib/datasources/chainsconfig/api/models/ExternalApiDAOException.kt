package jp.co.soramitsu.xnetworking.lib.datasources.chainsconfig.api.models

sealed class ExternalApiDAOException(
    override val message: String?
): RuntimeException() {

    class NullType(chainId: String): ExternalApiDAOException(
        "ChainsConfig.ExternalApi.Type was null for chain with id - $chainId."
    )

    class NullUrl(chainId: String): ExternalApiDAOException(
        "RequestUrl was null for chain with id - $chainId."
    )

}