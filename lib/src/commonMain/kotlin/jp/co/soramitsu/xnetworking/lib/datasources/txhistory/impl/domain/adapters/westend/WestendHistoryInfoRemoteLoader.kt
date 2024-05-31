package jp.co.soramitsu.xnetworking.lib.datasources.txhistory.impl.domain.adapters.westend

import com.apollographql.apollo3.api.Optional
import jp.co.soramitsu.xnetworking.lib.datasources.txhistory.api.adapters.HistoryInfoRemoteLoader
import jp.co.soramitsu.xnetworking.lib.datasources.txhistory.api.models.TxFilter
import jp.co.soramitsu.xnetworking.lib.datasources.txhistory.api.models.TxHistoryInfo
import jp.co.soramitsu.xnetworking.lib.datasources.txhistory.api.models.TxHistoryItem
import jp.co.soramitsu.xnetworking.lib.datasources.txhistory.api.models.TxHistoryItemParam
import jp.co.soramitsu.xnetworking.lib.engines.apollo.api.ApolloClientStore
import jp.co.soramitsu.xnetworking.fearlesswallet.GetFearlessHistoryElementsQuery
import jp.co.soramitsu.xnetworking.fearlesswallet.type.HistoryElementsOrderBy
import jp.co.soramitsu.xnetworking.lib.datasources.chainsconfig.api.ConfigDAO
import jp.co.soramitsu.xnetworking.lib.datasources.txhistory.api.models.ChainInfo
import jp.co.soramitsu.xnetworking.lib.engines.utils.fieldOrNull

class WestendHistoryInfoRemoteLoader(
    private val apolloClientStore: ApolloClientStore,
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

        val response = checkNotNull(
            apolloClientStore.query(
                serverUrl = configDAO.historyUrl(chainInfo.chainId),
                query = GetFearlessHistoryElementsQuery(
                    pageCount = pageCount,
                    cursor = cursor.orEmpty(),
                    address = signAddress,
                    orderBy = Optional.present(listOf(HistoryElementsOrderBy.TIMESTAMP_DESC)),
                )
            ).historyElements
        ) { "GetHistoryElementsQuery response is null" }

        // Unparsing JSON scalar and normally typed contents
        val items = mutableListOf<TxHistoryItem>()

        val responseItems = response.nodes.asSequence()
            .filterNotNull()

        if (TxFilter.REWARD in filters) {
            responseItems.map {
                TxHistoryItem(
                    id = it.id,
                    blockHash = "",
                    module = "reward",
                    method = "",
                    timestamp = it.timestamp,
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
            responseItems.map {
                TxHistoryItem(
                    id = it.id,
                    blockHash = "",
                    module = "transfer",
                    method = "",
                    timestamp = it.timestamp,
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
            responseItems.map {
                TxHistoryItem(
                    id = it.id,
                    blockHash = "",
                    module = "extrinsic",
                    method = "",
                    timestamp = it.timestamp,
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
            endReached = !response.pageInfo.hasNextPage,
            items = items
        )
    }

}