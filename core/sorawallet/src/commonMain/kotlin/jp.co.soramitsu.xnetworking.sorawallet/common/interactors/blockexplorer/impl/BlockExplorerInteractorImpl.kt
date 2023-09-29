package jp.co.soramitsu.xnetworking.sorawallet.common.interactors.blockexplorer.impl

import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.api.ApolloRequest
import com.apollographql.apollo3.api.ApolloResponse
import com.apollographql.apollo3.api.Operation
import com.apollographql.apollo3.network.NetworkTransport
import jp.co.soramitsu.xnetworking.basic.engines.apollo.api.ApolloClientStore
import jp.co.soramitsu.xnetworking.sorawallet.common.interactors.blockexplorer.api.BlockExplorerInteractor
import jp.co.soramitsu.xnetworking.sorawallet.core.datasources.blockexplorer.api.BlockExplorerRepository
import jp.co.soramitsu.xnetworking.sorawallet.core.datasources.blockexplorer.api.models.AssetsInfoResponse
import jp.co.soramitsu.xnetworking.sorawallet.core.datasources.blockexplorer.api.models.FiatDataResponse
import jp.co.soramitsu.xnetworking.sorawallet.core.datasources.blockexplorer.api.models.ReferrerRewardResponse
import jp.co.soramitsu.xnetworking.sorawallet.core.datasources.blockexplorer.api.models.SbApyInfoResponse
import jp.co.soramitsu.xnetworking.sorawallet.core.datasources.polkaswapconfig.api.ConfigRepository
import kotlinx.coroutines.flow.Flow

class BlockExplorerInteractorImpl(
    private val apolloClientStore: ApolloClientStore,
    private val configRepository: ConfigRepository,
    private val blockExplorerRepository: BlockExplorerRepository
): BlockExplorerInteractor {

    override suspend fun getAssetsInfo(
        tokenIds: List<String>,
        timeStamp: Int
    ): List<AssetsInfoResponse> {
        val commonConfig = configRepository.getCommonConfig()
        val mobileConfig = configRepository.getMobileConfig()

        if (commonConfig == null || mobileConfig == null)
            return emptyList()

        apolloClientStore.addClient(ApolloClientStore.SUBQUERY_TAG, commonConfig.subquery)

        return blockExplorerRepository.getAssetsInfo(
            requestType = mobileConfig.explorerTypeAssets,
            tokenIds = tokenIds,
            timeStamp = timeStamp
        )
    }

    override suspend fun getFiat(): List<FiatDataResponse> {
        val commonConfig = configRepository.getCommonConfig()
        val mobileConfig = configRepository.getMobileConfig()

        if (commonConfig == null || mobileConfig == null)
            return emptyList()

        apolloClientStore.addClient(ApolloClientStore.SUBQUERY_TAG, commonConfig.subquery)

        return blockExplorerRepository.getFiat(
            requestType = mobileConfig.explorerTypeFiat
        )
    }

    override suspend fun getReferrerRewards(
        address: String
    ): List<ReferrerRewardResponse> {
        val commonConfig = configRepository.getCommonConfig()
        val mobileConfig = configRepository.getMobileConfig()

        if (commonConfig == null || mobileConfig == null)
            return emptyList()

        apolloClientStore.addClient(ApolloClientStore.SUBQUERY_TAG, commonConfig.subquery)

        return blockExplorerRepository.getReferrerRewards(
            requestType = mobileConfig.explorerTypeReward,
            address = address
        )
    }

    override suspend fun getSbApyInfo(): List<SbApyInfoResponse> {
        val commonConfig = configRepository.getCommonConfig()
        val mobileConfig = configRepository.getMobileConfig()

        if (commonConfig == null || mobileConfig == null)
            return emptyList()

        apolloClientStore.addClient(ApolloClientStore.SUBQUERY_TAG, commonConfig.subquery)

        return blockExplorerRepository.getSbApyInfo(
            requestType = mobileConfig.explorerTypeSbapy
        )
    }

}