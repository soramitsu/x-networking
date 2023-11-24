package jp.co.soramitsu.xnetworking.fearlesswallet.txhistory.subsquid.models

import jp.co.soramitsu.xnetworking.basic.txhistory.TxHistoryInfo
import jp.co.soramitsu.xnetworking.basic.txhistory.TxHistoryItem
import jp.co.soramitsu.xnetworking.basic.txhistory.TxHistoryItemNested
import jp.co.soramitsu.xnetworking.basic.txhistory.TxHistoryItemParam
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive

internal object FearlessWalletSubSquidHistoryMapper {
    fun map(response: FearlessSubSquidResponse): TxHistoryInfo {

        return TxHistoryInfo(
            endCursor = response.data.historyElements.pageInfo.endCursor,
            endReached = response.data.historyElements.pageInfo.hasNextPage.not(),
            items = response.data.historyElements.nodes.map {
                TxHistoryItem(
                    id = it.node.id,
                    blockHash = it.node.blockHash,
                    module = it.node.module,
                    method = it.node.method,
                    timestamp = it.node.timestamp,
                    networkFee = it.node.networkFee.orEmpty(),
                    success = it.node.execution?.success ?: true,
                    data = (it.node.data as? JsonObject)?.let { json ->
                        json.map { mapItem ->
                            TxHistoryItemParam(
                                paramName = mapItem.key,
                                paramValue = (mapItem.value as? JsonPrimitive)?.content.orEmpty()
                            )
                        }
                    },
                    nestedData = (it.node.data as? JsonArray)?.let { jsonArray ->
                        jsonArray.filterIsInstance<JsonObject>().map { jsonObject ->
                            TxHistoryItemNested(
                                hash = (jsonObject["hash"] as? JsonPrimitive)?.content.orEmpty(),
                                module = (jsonObject["module"] as? JsonPrimitive)?.content.orEmpty(),
                                method = (jsonObject["method"] as? JsonPrimitive)?.content.orEmpty(),
                                data = ((jsonObject["data"] as? JsonObject)?.get("args") as? JsonObject)?.map { mapItem ->
                                    TxHistoryItemParam(
                                        paramName = mapItem.key,
                                        paramValue = (mapItem.value as? JsonPrimitive)?.content.orEmpty()
                                    )
                                } ?: emptyList()
                            )
                        }
                    },
                )
            },
        )
    }
}