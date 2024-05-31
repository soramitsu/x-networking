package jp.co.soramitsu.xnetworking.lib.datasources.txhistory.impl.domain.adapters.sorasubsquid

import jp.co.soramitsu.xnetworking.lib.datasources.chainsconfig.api.ConfigDAO
import jp.co.soramitsu.xnetworking.lib.datasources.txhistory.api.models.ChainInfo
import jp.co.soramitsu.xnetworking.lib.datasources.txhistory.api.adapters.HistoryInfoRemoteLoader
import jp.co.soramitsu.xnetworking.lib.datasources.txhistory.api.models.TxFilter
import jp.co.soramitsu.xnetworking.lib.datasources.txhistory.api.models.TxHistoryInfo
import jp.co.soramitsu.xnetworking.lib.datasources.txhistory.api.models.TxHistoryItem
import jp.co.soramitsu.xnetworking.lib.datasources.txhistory.api.models.TxHistoryItemNested
import jp.co.soramitsu.xnetworking.lib.datasources.txhistory.api.models.TxHistoryItemParam
import jp.co.soramitsu.xnetworking.lib.engines.utils.asJsonArrayNullable
import jp.co.soramitsu.xnetworking.lib.engines.utils.asJsonObjectNullable
import jp.co.soramitsu.xnetworking.lib.engines.utils.fieldOrNull
import jp.co.soramitsu.xnetworking.lib.engines.utils.objectOrNull
import jp.co.soramitsu.xnetworking.lib.engines.utils.primitiveOrNull
import jp.co.soramitsu.xnetworking.lib.engines.rest.api.RestClient
import kotlinx.serialization.json.JsonObject

class SoraSubSquidHistoryInfoRemoteLoader(
    private val configDAO: ConfigDAO,
    private val restClient: RestClient
) : HistoryInfoRemoteLoader() {

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
            request = SoraSubSquidRequest(
                url = configDAO.historyUrl(chainInfo.chainId),
                address = signAddress,
                limit = pageCount,
                cursor = cursor
            )
        ).data.historyElementsConnection

        return TxHistoryInfo(
            endCursor = connection.pageInfo.endCursor,
            endReached = !connection.pageInfo.hasNextPage,
            items = connection.edges.map { edge ->
                TxHistoryItem(
                    id = edge.node.id,
                    blockHash = edge.node.blockHash,
                    module = edge.node.module,
                    method = edge.node.method,
                    timestamp = edge.node.timestamp,
                    networkFee = edge.node.networkFee ?: "0",
                    success = edge.node.execution?.success ?: false,
                    data = edge.node.data.asJsonObjectNullable?.map { (key, item) ->
                        TxHistoryItemParam(
                            paramName = key,
                            paramValue = item.primitiveOrNull().orEmpty()
                        )
                    },
                    nestedData = edge.node.data.asJsonArrayNullable?.filterIsInstance<JsonObject>()
                        ?.map { json ->
                            TxHistoryItemNested(
                                hash = json.fieldOrNull("hash").orEmpty(),
                                module = json.fieldOrNull("module").orEmpty(),
                                method = json.fieldOrNull("method").orEmpty(),
                                data = json.objectOrNull("data")
                                    .objectOrNull("args")?.map { (key, item) ->
                                        TxHistoryItemParam(
                                            paramName = key,
                                            paramValue = item.primitiveOrNull().orEmpty()
                                        )
                                    } ?: emptyList()
                            )
                        }
                )
            }
        )
    }

}