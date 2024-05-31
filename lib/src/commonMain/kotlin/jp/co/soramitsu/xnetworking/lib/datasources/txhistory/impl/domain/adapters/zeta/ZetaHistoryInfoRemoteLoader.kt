package jp.co.soramitsu.xnetworking.lib.datasources.txhistory.impl.domain.adapters.zeta

import jp.co.soramitsu.xnetworking.lib.datasources.chainsconfig.api.ConfigDAO
import jp.co.soramitsu.xnetworking.lib.datasources.txhistory.api.models.ChainInfo
import jp.co.soramitsu.xnetworking.lib.datasources.txhistory.api.adapters.HistoryInfoRemoteLoader
import jp.co.soramitsu.xnetworking.lib.datasources.txhistory.api.models.TxFilter
import jp.co.soramitsu.xnetworking.lib.datasources.txhistory.api.models.TxHistoryInfo
import jp.co.soramitsu.xnetworking.lib.datasources.txhistory.api.models.TxHistoryItem
import jp.co.soramitsu.xnetworking.lib.datasources.txhistory.api.models.TxHistoryItemParam
import jp.co.soramitsu.xnetworking.lib.engines.rest.api.RestClient

class ZetaHistoryInfoRemoteLoader(
    private val configDAO: ConfigDAO,
    private val restClient: RestClient
): HistoryInfoRemoteLoader() {

    override suspend fun loadHistoryInfo(
        pageCount: Int,
        cursor: String?,
        signAddress: String,
        chainInfo: ChainInfo,
        filters: Set<TxFilter>
    ): TxHistoryInfo {
        require(chainInfo is ChainInfo.WithEthereumType) {
            "Zeta blockExplorer can not be used with non-ethereum chains."
        }

        val response = restClient.get(
            request = when(chainInfo.ethereumType) {
                "normal" -> TransactionsZetaRequest(
                    url = configDAO.historyUrl(chainInfo.chainId),
                    address = signAddress
                )
                else -> TokenTransfersZetaRequest(
                    url = configDAO.historyUrl(chainInfo.chainId),
                    address = signAddress,
                    assetId = chainInfo.contractAddress
                )
            }
        )

        return TxHistoryInfo(
            endCursor = null,
            endReached = true,
            items = response.items.map {
                TxHistoryItem(
                    id = it.txHash ?: "",
                    blockHash = it.hash ?: "",
                    module = "",
                    method = "",
                    timestamp = it.timestamp,
                    nestedData = emptyList(),
                    networkFee = it.fee?.value ?: "0",
                    success = true,
                    data = listOf(
                        TxHistoryItemParam(
                            "amount",
                            it.total?.value ?: "0"
                        ),
                        TxHistoryItemParam(
                            "to",
                            it.to.hash
                        ),
                        TxHistoryItemParam(
                            "from",
                            it.from.hash
                        ),
                    )
                )
            }
        )
    }

}