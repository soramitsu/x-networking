package jp.co.soramitsu.xnetworking.sorawallet.common.interactors.txhistory.impl

import jp.co.soramitsu.xnetworking.basic.engines.apollo.api.ApolloClientStore
import jp.co.soramitsu.xnetworking.sorawallet.common.interactors.txhistory.api.TxHistoryInteractor
import jp.co.soramitsu.xnetworking.basic.datasources.txhistory.api.TxHistoryRepository
import jp.co.soramitsu.xnetworking.basic.datasources.txhistory.api.models.TxHistoryItem
import jp.co.soramitsu.xnetworking.basic.datasources.txhistory.api.wrappers.TxHistoryResult
import jp.co.soramitsu.xnetworking.sorawallet.core.datasources.polkaswapconfig.api.ConfigRepository

class TxHistoryInteractorImpl(
    private val apolloClientStore: ApolloClientStore,
    private val configRepository: ConfigRepository,
    private val txHistoryRepository: TxHistoryRepository
): TxHistoryInteractor {

    private companion object {
        const val SORA_NETWORK_NAME = "SORA"
    }

    override suspend fun getTransactionPeers(
        query: String
    ) = txHistoryRepository.getTransactionPeers(
        query = query,
        networkName = SORA_NETWORK_NAME
    )

    override suspend fun getTransactionHistoryCached(
        count: Int,
        address: String
    ) = txHistoryRepository.getTransactionHistoryCached(
        count = count,
        address = address,
        networkName = SORA_NETWORK_NAME,
    )

    override suspend fun getTransactionHistoryPaged(
        address: String,
        page: Long
    ): TxHistoryResult<TxHistoryItem> {
        val commonConfig = configRepository.getCommonConfig()
        val mobileConfig = configRepository.getMobileConfig()

        if (commonConfig == null || mobileConfig == null)
            return TxHistoryResult(
                endCursor = "",
                endReached = false,
                page = -1,
                items = emptyList(),
                errorMessage = null
            )

        apolloClientStore.addClient(ApolloClientStore.SUBQUERY_TAG, commonConfig.subquery)

        return txHistoryRepository.getTransactionHistoryPaged(
            address = address,
            page = page,
            networkName = SORA_NETWORK_NAME
        )
    }

    override suspend fun getTransactionCached(
        txHash: String,
        address: String
    ) = txHistoryRepository.getTransactionCached(
        txHash = txHash,
        address = address,
        networkName = SORA_NETWORK_NAME
    )

    override suspend fun clearData(
        address: String
    ) = txHistoryRepository.clearData(
        address = address,
        networkName = SORA_NETWORK_NAME
    )

    override suspend fun clearAllData() = txHistoryRepository.clearAllData()
}