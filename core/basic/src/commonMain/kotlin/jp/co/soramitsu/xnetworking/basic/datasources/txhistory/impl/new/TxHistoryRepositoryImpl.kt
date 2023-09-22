package jp.co.soramitsu.xnetworking.basic.datasources.txhistory.impl.new

import jp.co.soramitsu.xnetworking.basic.datasources.txhistory.api.HistoryInfoRemoteLoader
import jp.co.soramitsu.xnetworking.basic.datasources.txhistory.api.HistoryItemsFilter
import jp.co.soramitsu.xnetworking.basic.datasources.txhistory.api.TxHistoryRepository
import jp.co.soramitsu.xnetworking.basic.datasources.txhistory.api.models.TxHistoryInfo
import jp.co.soramitsu.xnetworking.basic.datasources.txhistory.api.models.TxHistoryItem
import jp.co.soramitsu.xnetworking.basic.datasources.txhistory.api.wrappers.TxHistoryResult
import jp.co.soramitsu.xnetworking.basic.datasources.txhistory.impl.new.builder.ExpectActualDBDriverFactory
import jp.co.soramitsu.xnetworking.basic.datasources.txhistory.impl.new.domain.usecase.FetchExtrinsicsAndSavePagedDecorator
import jp.co.soramitsu.xnetworking.basic.datasources.txhistory.impl.new.domain.usecase.FetchExtrinsicsAndSaveUseCase
import jp.co.soramitsu.xnetworking.basic.datasources.txhistory.impl.new.domain.utils.HistoryMapper
import jp.co.soramitsu.xnetworking.basic.networkclient.SoramitsuNetworkException
import jp.co.soramitsu.xnetworking.db.Extrinsics
import jp.co.soramitsu.xnetworking.db.SignerInfo
import jp.co.soramitsu.xnetworking.db.SoraHistoryDatabase

class TxHistoryRepositoryImpl(
    private val databaseDriverFactory: ExpectActualDBDriverFactory,
    private val historyInfoRemoteLoader: HistoryInfoRemoteLoader,
    private val historyItemsFilter: HistoryItemsFilter
): TxHistoryRepository, HistoryItemsFilter by historyItemsFilter {

    private val soraHistoryDBImpl = SoraHistoryDBImpl(
        soraHistoryDatabase = SoraHistoryDatabase(
            databaseDriverFactory.createDriver()
        )
    )

    private val fetchExtrinsicsAndSaveUseCase = FetchExtrinsicsAndSaveUseCase(
        soraHistoryDBImpl = soraHistoryDBImpl,
        historyInfoRemoteLoader = historyInfoRemoteLoader
    )

    private val fetchExtrinsicsAndSavePagedDecorator = FetchExtrinsicsAndSavePagedDecorator(
        fetchExtrinsicsAndSaveUseCase = fetchExtrinsicsAndSaveUseCase,
        soraHistoryDBImpl = soraHistoryDBImpl,
    )

    private lateinit var curSignerInfo: SignerInfo

    override fun getTransactionPeers(
        query: String,
        networkName: String
    ): List<String> {
        return soraHistoryDBImpl.getTransfersAddress(
            query = query,
            networkName = networkName
        )
    }

    override fun getTransactionCached(
        txHash: String,
        address: String,
        networkName: String,
    ): TxHistoryInfo {
        val extrinsic = soraHistoryDBImpl.getExtrinsic(
            signAddress = address,
            networkName = networkName,
            txHash = txHash
        )

        return buildResultHistoryInfo(
            endReached = true,
            extrinsics = if (extrinsic == null)
                emptyList() else listOf(extrinsic)
        )
    }

    override fun getTransactionHistoryCached(
        count: Int,
        address: String,
        networkName: String,
    ): List<TxHistoryItem> {
        var offset: Long = 0
        val result = mutableListOf<TxHistoryItem>()

        do {
            val extrinsics = soraHistoryDBImpl.getExtrinsics(
                signAddress = address,
                networkName = networkName,
                offset = offset,
                count = count
            )

            offset += extrinsics.size

            buildResultHistoryInfo(
                endReached = true,
                extrinsics = extrinsics
            ).apply { result.addAll(items) }
        } while (result.size < count && extrinsics.isNotEmpty())

        return result.filterCachedHistoryItems()
    }

    override suspend fun getTransactionHistoryPaged(
        address: String,
        page: Long,
        networkName: String,
    ): TxHistoryResult<TxHistoryItem> {
        require(page >= 1) { "Page value must >= 1" }

        curSignerInfo = soraHistoryDBImpl.getSignerInfo(address, networkName)

        var error = if (page == 1L) {
            try {
                curSignerInfo = fetchExtrinsicsAndSaveUseCase<TxHistoryInfo>(
                    cursor = "",
                    signAddress = address,
                    networkName = networkName,
                    currentSignerInfo = curSignerInfo,
                )
                null
            } catch (t: SoramitsuNetworkException) {
                t.m
            }
        } else {
            null
        }

        var curPage = page
        var endCursor: String? = null
        var endReached = false
        val list = mutableListOf<TxHistoryItem>()

        while (true) {
            val info = try {
                val pagedExtrinsicsWrapper =
                    fetchExtrinsicsAndSavePagedDecorator<TxHistoryInfo>(
                        signAddress = address,
                        networkName = networkName,
                        page = curPage,
                        currentSignerInfo = curSignerInfo,
                        extrinsicsCountPerPage = 100 // TODO change
                    )

                buildResultHistoryInfo(
                    endReached = pagedExtrinsicsWrapper.isEndReached,
                    extrinsics = pagedExtrinsicsWrapper.items
                )
            } catch (t: SoramitsuNetworkException) {
                error = t.m
                break
            }

            endCursor = info.endCursor
            endReached = info.endReached

            list.addAll(info.items)

            if (list.size >= page || info.endReached)
                break

            curPage++
        }

        return TxHistoryResult(
            endCursor = endCursor,
            endReached = endReached,
            page = curPage,
            items = list.filterPagedHistoryItems(),
            errorMessage = error,
        )
    }

    override fun clearData(
        address: String,
        networkName: String
    ) {
        soraHistoryDBImpl.clearAddressData(
            signAddress = address,
            networkName = networkName
        )
    }

    override fun clearAllData() {
        soraHistoryDBImpl.clearDatabase()
    }

    private fun buildResultHistoryInfo(
        endReached: Boolean,
        extrinsics: List<Extrinsics>
    ): TxHistoryInfo {
        val mapped = extrinsics.map { extrinsic ->
            if (extrinsic.batch.not()) {
                val params = soraHistoryDBImpl.getExtrinsicParams(extrinsic.txHash)
                HistoryMapper.mapParams(extrinsic, params)
            } else {
                val nestedExtrinsics = soraHistoryDBImpl.getExtrinsicNested(extrinsic.txHash)
                val nestedMapped = nestedExtrinsics.map {
                    val nestedParams = soraHistoryDBImpl.getExtrinsicParams(it.txHash)
                    HistoryMapper.mapItemNested(it, nestedParams)
                }
                HistoryMapper.mapItems(extrinsic, nestedMapped)
            }
        }
        return TxHistoryInfo("", endReached, mapped)
    }

}