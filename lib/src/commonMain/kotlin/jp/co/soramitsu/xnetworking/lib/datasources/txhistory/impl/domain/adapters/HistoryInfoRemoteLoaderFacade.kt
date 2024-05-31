package jp.co.soramitsu.xnetworking.lib.datasources.txhistory.impl.domain.adapters

import jp.co.soramitsu.xnetworking.lib.datasources.chainsconfig.api.ConfigDAO
import jp.co.soramitsu.xnetworking.lib.datasources.chainsconfig.api.models.ExternalApiType
import jp.co.soramitsu.xnetworking.lib.datasources.txhistory.api.models.ChainInfo
import jp.co.soramitsu.xnetworking.lib.datasources.txhistory.api.adapters.HistoryInfoRemoteLoader
import jp.co.soramitsu.xnetworking.lib.datasources.txhistory.api.models.TxFilter
import jp.co.soramitsu.xnetworking.lib.datasources.txhistory.api.models.TxHistoryInfo

class HistoryInfoRemoteLoaderFacade(
    private val configDAO: ConfigDAO,
    private val loaders: Map<ExternalApiType, HistoryInfoRemoteLoader>,
): HistoryInfoRemoteLoader() {
    override suspend fun loadHistoryInfo(
        pageCount: Int,
        cursor: String?,
        signAddress: String,
        chainInfo: ChainInfo,
        filters: Set<TxFilter>
    ): TxHistoryInfo {
        val loader = checkNotNull(loaders[configDAO.historyType(chainInfo.chainId)]) {
            "Remote History Loader could not have been found."
        }

        return loader.loadHistoryInfo(pageCount, cursor, signAddress, chainInfo, filters)
    }
}