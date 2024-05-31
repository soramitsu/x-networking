package jp.co.soramitsu.xnetworking.lib.datasources.txhistory.impl.domain.adapters.subsquid

import jp.co.soramitsu.xnetworking.lib.datasources.chainsconfig.api.ConfigDAO
import jp.co.soramitsu.xnetworking.lib.datasources.txhistory.api.models.ChainInfo
import jp.co.soramitsu.xnetworking.lib.datasources.txhistory.api.adapters.HistoryInfoRemoteLoader
import jp.co.soramitsu.xnetworking.lib.datasources.txhistory.api.models.TxFilter
import jp.co.soramitsu.xnetworking.lib.datasources.txhistory.api.models.TxHistoryInfo
import jp.co.soramitsu.xnetworking.lib.datasources.txhistory.api.models.TxHistoryItem
import jp.co.soramitsu.xnetworking.lib.datasources.txhistory.api.models.TxHistoryItemParam
import jp.co.soramitsu.xnetworking.lib.engines.rest.api.RestClient

class SubSquidHistoryInfoRemoteLoader(
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
        if (filters.isEmpty()) {
            return TxHistoryInfo(
                endCursor = cursor,
                endReached = false,
                items = emptyList()
            )
        }

        val connection = restClient.post(
            request = SubSquidRequest(
                url = configDAO.historyUrl(chainInfo.chainId),
                address = signAddress,
                limit = pageCount,
                cursor = cursor
            )
        ).data.historyElementsConnection

        val items = mutableListOf<TxHistoryItem>()

        val nodes = connection.edges
            .map(SubSquidResponse.HistoryElementsConnection.Node::node)
            .asSequence()

        if (TxFilter.TRANSFER in filters) {
            nodes.mapNotNull { node ->
                node.transfer?.let {
                    TxHistoryItem(
                        id = node.extrinsicIdx ?: "",
                        blockHash = node.extrinsicHash ?: "",
                        module = "",
                        method = "",
                        timestamp = node.timestamp.toString(),
                        nestedData = emptyList(),
                        networkFee = it.fee ?: "0",
                        success = node.success,
                        data = listOf(
                            TxHistoryItemParam(
                                "amount",
                                it.amount
                            ),
                            TxHistoryItemParam(
                                "to",
                                it.to
                            ),
                            TxHistoryItemParam(
                                "from",
                                it.from
                            ),
                        )
                    )
                }
            }.toCollection(items)
        }

        if (TxFilter.REWARD in filters) {
            nodes.mapNotNull { node ->
                node.reward?.let {
                    TxHistoryItem(
                        id = node.extrinsicIdx ?: "",
                        blockHash = node.extrinsicHash ?: "",
                        module = "",
                        method = "",
                        timestamp = node.timestamp.toString(),
                        nestedData = emptyList(),
                        networkFee = "0",
                        success = node.success,
                        data = listOf(
                            TxHistoryItemParam(
                                "amount",
                                it.amount
                            ),
                            TxHistoryItemParam(
                                "era",
                                it.era.toString()
                            ),
                            TxHistoryItemParam(
                                "stash",
                                it.stash ?: ""
                            ),
                        )
                    )
                }
            }.toCollection(items)
        }

        return TxHistoryInfo(
            endCursor = connection.pageInfo.endCursor,
            endReached = connection.pageInfo.hasNextPage?.not() ?: true,
            items = items
        )
    }

}