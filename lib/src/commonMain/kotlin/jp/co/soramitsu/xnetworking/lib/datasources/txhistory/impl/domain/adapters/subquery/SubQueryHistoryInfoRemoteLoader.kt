package jp.co.soramitsu.xnetworking.lib.datasources.txhistory.impl.domain.adapters.subquery

import jp.co.soramitsu.xnetworking.lib.datasources.chainsconfig.api.ConfigDAO
import jp.co.soramitsu.xnetworking.lib.datasources.txhistory.api.models.ChainInfo
import jp.co.soramitsu.xnetworking.lib.datasources.txhistory.api.adapters.HistoryInfoRemoteLoader
import jp.co.soramitsu.xnetworking.lib.datasources.txhistory.api.models.TxFilter
import jp.co.soramitsu.xnetworking.lib.datasources.txhistory.api.models.TxHistoryInfo
import jp.co.soramitsu.xnetworking.lib.datasources.txhistory.api.models.TxHistoryItem
import jp.co.soramitsu.xnetworking.lib.datasources.txhistory.api.models.TxHistoryItemParam
import jp.co.soramitsu.xnetworking.lib.engines.rest.api.RestClient

class SubQueryHistoryInfoRemoteLoader(
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

        val historyElements = restClient.post(
            request = SubQueryRequest(
                url = configDAO.historyUrl(chainInfo.chainId),
                cursor = cursor,
                pageCount = pageCount,
                address = signAddress,
                filters = filters,
                requestRewards = configDAO.staking(chainInfo.chainId) != null
            )
        ).data.historyElements

        val items = mutableListOf<TxHistoryItem>()

        val nodes = historyElements.nodes.asSequence()

        if (TxFilter.TRANSFER in filters) {
            nodes.mapNotNull { node ->
                node.transfer?.let {
                    TxHistoryItem(
                        id = node.id,
                        blockHash = it.block,
                        module = "transfer",
                        method = "",
                        timestamp = node.timestamp,
                        nestedData = emptyList(),
                        networkFee = it.fee,
                        success = it.success,
                        data = listOfNotNull(
                            it.assetId?.let { tokenId ->
                                TxHistoryItemParam(
                                    "tokenId",
                                    tokenId
                                )
                            },
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
                            it.extrinsicHash?.let { extrinsicHash ->
                                TxHistoryItemParam(
                                    "extrinsicHash",
                                    extrinsicHash
                                )
                            },
                        )
                    )
                }
            }.toCollection(items)
        }

        if (TxFilter.REWARD in filters) {
            nodes.mapNotNull { node ->
                node.reward?.let {
                    TxHistoryItem(
                        id = node.id,
                        blockHash = "",
                        module = "reward",
                        method = "",
                        timestamp = node.timestamp,
                        nestedData = emptyList(),
                        networkFee = "0",
                        success = true,
                        data = listOfNotNull(
                            it.assetId?.let { tokenId ->
                                TxHistoryItemParam(
                                    "tokenId",
                                    tokenId
                                )
                            },
                            TxHistoryItemParam(
                                "amount",
                                it.amount
                            ),
                            TxHistoryItemParam(
                                "validator",
                                it.validator
                            ),
                            TxHistoryItemParam(
                                "isReward",
                                it.isReward.toString()
                            ),
                            TxHistoryItemParam(
                                "era",
                                it.era.toString()
                            ),
                        )
                    )
                }
            }.toCollection(items)
        }

        if (TxFilter.EXTRINSIC in filters) {
            nodes.mapNotNull { node ->
                node.extrinsic?.let {
                    TxHistoryItem(
                        id = node.id,
                        blockHash = it.hash,
                        module = "extrinsic",
                        method = "",
                        timestamp = node.timestamp,
                        nestedData = emptyList(),
                        networkFee = it.fee,
                        success = it.success,
                        data = listOfNotNull(
                            it.assetId?.let { tokenId ->
                                TxHistoryItemParam(
                                    "tokenId",
                                    tokenId
                                )
                            },
                            TxHistoryItemParam(
                                "module",
                                it.module
                            ),
                            TxHistoryItemParam(
                                "call",
                                it.call
                            ),
                        )
                    )
                }
            }.toCollection(items)
        }

        return TxHistoryInfo(
            endCursor = historyElements.pageInfo.endCursor,
            endReached = historyElements.pageInfo.endCursor == null,
            items = items
        )
    }

}