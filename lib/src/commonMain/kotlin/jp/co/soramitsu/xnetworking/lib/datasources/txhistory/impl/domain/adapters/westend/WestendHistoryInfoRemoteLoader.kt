package jp.co.soramitsu.xnetworking.lib.datasources.txhistory.impl.domain.adapters.westend

import jp.co.soramitsu.xnetworking.lib.datasources.txhistory.api.adapters.HistoryInfoRemoteLoader
import jp.co.soramitsu.xnetworking.lib.datasources.txhistory.api.models.TxFilter
import jp.co.soramitsu.xnetworking.lib.datasources.txhistory.api.models.TxHistoryInfo
import jp.co.soramitsu.xnetworking.lib.datasources.txhistory.api.models.TxHistoryItem
import jp.co.soramitsu.xnetworking.lib.datasources.txhistory.api.models.TxHistoryItemParam
import jp.co.soramitsu.xnetworking.lib.datasources.chainsconfig.api.ConfigDAO
import jp.co.soramitsu.xnetworking.lib.datasources.txhistory.api.models.ChainInfo
import jp.co.soramitsu.xnetworking.lib.engines.rest.api.RestClient
import jp.co.soramitsu.xnetworking.lib.engines.utils.fieldOrNull
import jp.co.soramitsu.xnetworking.lib.engines.utils.wrapToGraphQLString

class WestendHistoryInfoRemoteLoader(
    private val restClient: RestClient,
    private val configDAO: ConfigDAO
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

        val response = restClient.post(
            request = WestendRequest(
                url = configDAO.historyUrl(chainInfo.chainId),
                pageCount = pageCount,
                cursor = cursor?.wrapToGraphQLString(),
                address = signAddress.wrapToGraphQLString()
            )
        ).data.historyElements

        // Unparsing JSON scalar and normally typed contents
        val items = mutableListOf<TxHistoryItem>()

        val responseItems = response.nodes.asSequence()

        if (TxFilter.REWARD in filters) {
            responseItems.mapNotNull {
                TxHistoryItem(
                    id = it.id ?: return@mapNotNull null,
                    blockHash = "",
                    module = "reward",
                    method = "",
                    timestamp = it.timestamp.toString(),
                    networkFee = "0",
                    success = true,
                    nestedData = null,
                    data = it.reward?.let { jsonObject ->
                        listOf(
                            TxHistoryItemParam(
                                "amount",
                                jsonObject.fieldOrNull("amount").orEmpty()
                            ),
                            TxHistoryItemParam(
                                "era",
                                jsonObject.fieldOrNull("era").orEmpty()
                            ),
                            TxHistoryItemParam(
                                "isReward",
                                jsonObject.fieldOrNull("isReward").orEmpty()
                            ),
                            TxHistoryItemParam(
                                "validator",
                                jsonObject.fieldOrNull("validator").orEmpty()
                            ),
                        )
                    }
                )
            }.toCollection(items)
        }

        if (TxFilter.TRANSFER in filters) {
            responseItems.mapNotNull {
                TxHistoryItem(
                    id = it.id ?: return@mapNotNull null,
                    blockHash = "",
                    module = "transfer",
                    method = "",
                    timestamp = it.timestamp.toString(),
                    networkFee = it.transfer.fieldOrNull("fee").orEmpty(),
                    success = it.transfer.fieldOrNull("success")?.toBooleanStrictOrNull() ?: false,
                    nestedData = null,
                    data = it.transfer?.let { jsonObject ->
                        listOf(
                            TxHistoryItemParam(
                                "block",
                                jsonObject.fieldOrNull("block").orEmpty()
                            ),
                            TxHistoryItemParam(
                                "amount",
                                jsonObject.fieldOrNull("amount").orEmpty()
                            ),
                            TxHistoryItemParam(
                                "to",
                                jsonObject.fieldOrNull("to").orEmpty()
                            ),
                            TxHistoryItemParam(
                                "from",
                                jsonObject.fieldOrNull("from").orEmpty()
                            ),
                            TxHistoryItemParam(
                                "extrinsicHash",
                                jsonObject.fieldOrNull("extrinsicHash").orEmpty()
                            )
                        )
                    }
                )
            }.toCollection(items)
        }

        if (TxFilter.EXTRINSIC in filters) {
            responseItems.mapNotNull {
                TxHistoryItem(
                    id = it.id ?: return@mapNotNull null,
                    blockHash = "",
                    module = "extrinsic",
                    method = "",
                    timestamp = it.timestamp.toString(),
                    networkFee = it.extrinsic.fieldOrNull("fee").orEmpty(),
                    success = it.extrinsic.fieldOrNull("success")?.toBooleanStrictOrNull() ?: false,
                    nestedData = null,
                    data = it.extrinsic?.let { jsonObject ->
                        listOf(
                            TxHistoryItemParam(
                                "call",
                                jsonObject.fieldOrNull("call").orEmpty()
                            ),
                            TxHistoryItemParam(
                                "hash",
                                jsonObject.fieldOrNull("hash").orEmpty()
                            ),
                            TxHistoryItemParam(
                                "module",
                                jsonObject.fieldOrNull("module").orEmpty()
                            ),
                        )
                    }
                )
            }.toCollection(items)
        }

        return TxHistoryInfo(
            endCursor = response.pageInfo.endCursor,
            endReached = !(response.pageInfo.hasNextPage ?: false),
            items = items
        )
    }

}