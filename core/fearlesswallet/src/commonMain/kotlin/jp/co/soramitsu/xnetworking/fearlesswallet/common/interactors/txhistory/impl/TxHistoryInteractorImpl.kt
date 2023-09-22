package jp.co.soramitsu.xnetworking.fearlesswallet.common.interactors.txhistory.impl

import jp.co.soramitsu.xnetworking.basic.engines.apollo.api.ApolloClientStore
import jp.co.soramitsu.xnetworking.basic.datasources.txhistory.api.TxHistoryRepository
import jp.co.soramitsu.xnetworking.basic.datasources.txhistory.api.models.TxHistoryItem
import jp.co.soramitsu.xnetworking.basic.datasources.txhistory.api.wrappers.TxHistoryResult
import jp.co.soramitsu.xnetworking.fearlesswallet.common.interactors.txhistory.api.TxHistoryInteractor

class TxHistoryInteractorImpl(
    private val apolloClientStore: ApolloClientStore,
    private val txHistoryRepository: TxHistoryRepository
): TxHistoryInteractor {

    override suspend fun getTransactionPeers(
        query: String,
        networkName: String
    ) = txHistoryRepository.getTransactionPeers(
        query = query,
        networkName = networkName
    )

    override suspend fun getTransactionHistoryCached(
        count: Int,
        address: String,
        networkName: String,
    ) = txHistoryRepository.getTransactionHistoryCached(
        count = count,
        address = address,
        networkName = networkName
    )

    override suspend fun getTransactionCached(
        txHash: String,
        address: String,
        networkName: String
    ) = txHistoryRepository.getTransactionCached(
        txHash = txHash,
        address = address,
        networkName = networkName
    )

    override suspend fun getTransactionHistoryPaged(
        address: String,
        page: Long,
        requestUrl: String,
        networkName: String,
    ): TxHistoryResult<TxHistoryItem> {
        apolloClientStore.addClient(ApolloClientStore.FEARLESS_SUBQUERY_TAG, requestUrl)

        return txHistoryRepository.getTransactionHistoryPaged(
            address = address,
            page = page,
            networkName = networkName
        )
    }

    override suspend fun clearData(
        address: String,
        networkName: String
    ) = txHistoryRepository.clearData(
        address = address,
        networkName = networkName
    )

    override suspend fun clearAllData() = txHistoryRepository.clearAllData()
}