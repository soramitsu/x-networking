package jp.co.soramitsu.xnetworking.lib.datasources.txhistory.impl

import jp.co.soramitsu.xnetworking.lib.datasources.txhistory.api.adapters.HistoryInfoRemoteLoader
import jp.co.soramitsu.xnetworking.lib.datasources.txhistory.api.HistoryItemsFilter
import jp.co.soramitsu.xnetworking.lib.datasources.txhistory.api.models.TxFilter
import jp.co.soramitsu.xnetworking.lib.datasources.txhistory.api.TxHistoryRepository
import jp.co.soramitsu.xnetworking.lib.datasources.txhistory.api.models.TxHistoryInfo
import jp.co.soramitsu.xnetworking.lib.datasources.txhistory.api.models.TxHistoryItem
import jp.co.soramitsu.xnetworking.lib.datasources.txhistory.api.models.wrappers.TxHistoryResult
import jp.co.soramitsu.xnetworking.lib.datasources.txhistory.impl.builder.ExpectActualDBDriverFactory
import jp.co.soramitsu.xnetworking.lib.datasources.txhistory.impl.domain.usecase.FetchExtrinsicsAndSavePagedDecorator
import jp.co.soramitsu.xnetworking.lib.datasources.txhistory.impl.domain.usecase.FetchExtrinsicsAndSaveUseCase
import jp.co.soramitsu.xnetworking.lib.datasources.txhistory.impl.utils.HistoryMapper
import jp.co.soramitsu.xnetworking.lib.engines.rest.api.models.RestClientException
import jp.co.soramitsu.xnetworking.db.Extrinsics
import jp.co.soramitsu.xnetworking.db.SignerInfo
import jp.co.soramitsu.xnetworking.db.SoraHistoryDatabase
import jp.co.soramitsu.xnetworking.lib.datasources.txhistory.api.models.ChainInfo

class TxHistoryRepositoryImpl(
    private val databaseDriverFactory: ExpectActualDBDriverFactory,
    private val historyInfoRemoteLoader: HistoryInfoRemoteLoader,
    private val historyItemsFilter: HistoryItemsFilter
): TxHistoryRepository(), HistoryItemsFilter by historyItemsFilter {

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
        chainId: String
    ): List<String> {
        return soraHistoryDBImpl.getTransfersAddress(
            query = query,
            chainId = chainId
        )
    }

    override fun getTransactionCached(
        txHash: String,
        address: String,
        chainId: String,
    ): TxHistoryInfo {
        val extrinsic = soraHistoryDBImpl.getExtrinsic(
            signAddress = address,
            chainId = chainId,
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
        chainId: String,
    ): List<TxHistoryItem> {
        var offset: Long = 0
        val result = mutableListOf<TxHistoryItem>()

        do {
            val extrinsics = soraHistoryDBImpl.getExtrinsics(
                signAddress = address,
                chainId = chainId,
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
        pageCount: Int,
        chainInfo: ChainInfo,
        filters: Set<TxFilter>
    ): TxHistoryResult<TxHistoryItem> {
        require(page >= 1) { "Page value must >= 1" }

        curSignerInfo = soraHistoryDBImpl.getSignerInfo(address, chainInfo.chainId)

        var error = if (page == 1L) {
            try {
                curSignerInfo = fetchExtrinsicsAndSaveUseCase<TxHistoryInfo>(
                    cursor = curSignerInfo.oldCursor,
                    pageCount = pageCount,
                    signAddress = address,
                    currentSignerInfo = curSignerInfo,
                    chainInfo = chainInfo,
                    filters = filters
                )
                null
            } catch (t: RestClientException) {
                t.message
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
                        page = curPage,
                        currentSignerInfo = curSignerInfo,
                        extrinsicsCountPerPage = pageCount,
                        chainInfo = chainInfo,
                        filters = filters
                    )

                buildResultHistoryInfo(
                    endReached = pagedExtrinsicsWrapper.isEndReached,
                    extrinsics = pagedExtrinsicsWrapper.items
                )
            } catch (t: RestClientException) {
                error = t.message
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
        chainId: String
    ) {
        soraHistoryDBImpl.clearAddressData(
            signAddress = address,
            chainId = chainId
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