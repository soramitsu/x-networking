package jp.co.soramitsu.xnetworking.sorawallet.core.datasources.blockexplorer.impl.graphql

import jp.co.soramitsu.xnetworking.basic.engines.apollo.api.ApolloClientStore
import jp.co.soramitsu.xnetworking.sorawallet.core.datasources.blockexplorer.api.BlockExplorerRepository
import jp.co.soramitsu.xnetworking.sorawallet.core.datasources.blockexplorer.impl.graphql.usecase.GetAssetsInfoUseCase
import jp.co.soramitsu.xnetworking.sorawallet.core.datasources.blockexplorer.impl.graphql.usecase.GetFiatDataUseCase
import jp.co.soramitsu.xnetworking.sorawallet.core.datasources.blockexplorer.impl.graphql.usecase.GetReferrerRewardsUseCase
import jp.co.soramitsu.xnetworking.sorawallet.core.datasources.blockexplorer.impl.graphql.usecase.GetSbApyInfoUseCase

class GraphQLBlockExplorerRepositoryImpl(
    private val apolloClientStore: ApolloClientStore
): BlockExplorerRepository {

    private val getAssetsInfoUseCase = GetAssetsInfoUseCase()
    private val getFiatDataUseCase = GetFiatDataUseCase()
    private val getReferrerRewardsUseCase = GetReferrerRewardsUseCase()
    private val getSbApyInfoUseCase = GetSbApyInfoUseCase()

    override suspend fun getAssetsInfo(
        requestType: String,
        tokenIds: List<String>,
        timeStamp: Int
    ) = when(requestType) {
        "0" -> apolloClientStore.getClient(
            tag = ApolloClientStore.SUBQUERY_TAG
        )?.let { getAssetsInfoUseCase.invoke(it, tokenIds, timeStamp) } ?: emptyList()
        else -> throw IllegalArgumentException("SoraWalletFiatCases [$requestType] not found")
    }

    override suspend fun getFiat(
        requestType: String
    ) = when(requestType) {
        "2" -> apolloClientStore.getClient(
            tag = ApolloClientStore.SUBQUERY_TAG
        )?.let { getFiatDataUseCase.invoke(it) } ?: emptyList()
        else -> throw IllegalArgumentException("SoraWalletFiatCases [$requestType] not found")
    }

    override suspend fun getReferrerRewards(
        requestType: String,
        address: String
    ) = when(requestType) {
        "1" -> apolloClientStore.getClient(
            tag = ApolloClientStore.SUBQUERY_TAG
        )?.let { getReferrerRewardsUseCase.invoke(it, address) } ?: emptyList()
        else -> throw IllegalArgumentException("SoraWalletFiatCases [$requestType] not found")
    }

    override suspend fun getSbApyInfo(
        requestType: String
    ) = when(requestType) {
        "2" -> apolloClientStore.getClient(
            tag = ApolloClientStore.SUBQUERY_TAG
        )?.let { getSbApyInfoUseCase.invoke(it) } ?: emptyList()
        else -> throw IllegalArgumentException("SoraWalletFiatCases [$requestType] not found")
    }

}