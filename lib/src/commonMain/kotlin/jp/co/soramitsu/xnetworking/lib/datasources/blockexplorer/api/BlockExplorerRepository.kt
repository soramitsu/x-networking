package jp.co.soramitsu.xnetworking.lib.datasources.blockexplorer.api

import com.apollographql.apollo3.exception.ApolloException
import jp.co.soramitsu.xnetworking.lib.datasources.blockexplorer.api.models.AssetInfo
import jp.co.soramitsu.xnetworking.lib.datasources.blockexplorer.api.models.Fiat
import jp.co.soramitsu.xnetworking.lib.datasources.blockexplorer.api.models.ReferralReward
import jp.co.soramitsu.xnetworking.lib.datasources.chainsconfig.api.models.ExternalApiDAOException
import jp.co.soramitsu.xnetworking.lib.datasources.blockexplorer.api.models.Apy
import jp.co.soramitsu.xnetworking.lib.datasources.blockexplorer.api.models.Unbonding
import jp.co.soramitsu.xnetworking.lib.engines.rest.api.models.RestClientException
import kotlin.coroutines.cancellation.CancellationException

abstract class BlockExplorerRepository {

    @Throws(
        CancellationException::class,
        ExternalApiDAOException::class,
        IllegalArgumentException::class,
        IllegalStateException::class,
        NullPointerException::class,
        RestClientException::class,
    )
    abstract suspend fun getApy(
        chainId: String,
        selectedCandidates: List<String>? = null
    ): List<Apy>

    @Throws(
        ApolloException::class,
        CancellationException::class,
        ExternalApiDAOException::class,
        IllegalArgumentException::class,
        IllegalStateException::class,
        NullPointerException::class,
        RestClientException::class,
    )
    abstract suspend fun getAssetsInfo(
        chainId: String,
        tokenIds: List<String>,
        timeStamp: Int
    ): List<AssetInfo>

    @Throws(
        ApolloException::class,
        CancellationException::class,
        ExternalApiDAOException::class,
        IllegalArgumentException::class,
        IllegalStateException::class,
        NullPointerException::class,
        RestClientException::class,
    )
    abstract suspend fun getFiat(
        chainId: String
    ): List<Fiat>

    @Throws(
        ApolloException::class,
        CancellationException::class,
        ExternalApiDAOException::class,
        IllegalArgumentException::class,
        IllegalStateException::class,
        NullPointerException::class,
        RestClientException::class,
    )
    abstract suspend fun getReferralReward(
        chainId: String,
        address: String
    ): List<ReferralReward>

    @Throws(
        CancellationException::class,
        ExternalApiDAOException::class,
        IllegalArgumentException::class,
        IllegalStateException::class,
        NullPointerException::class,
        RestClientException::class,
    )
    abstract suspend fun getUnbondingsList(
        chainId: String,
        delegatorAddress: String,
        collatorAddress: String
    ): List<Unbonding>

    @Throws(
        CancellationException::class,
        ExternalApiDAOException::class,
        IllegalArgumentException::class,
        IllegalStateException::class,
        NullPointerException::class,
        RestClientException::class,
    )
    abstract suspend fun getValidatorsList(
        chainId: String,
        stashAccountAddress: String,
        historicalRange: List<String>
    ): List<String>

}