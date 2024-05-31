package jp.co.soramitsu.xnetworking.sorawallet.txhistory.subquery.models

import jp.co.soramitsu.xnetworking.basic.txhistory.TxHistoryInfo
import jp.co.soramitsu.xnetworking.basic.txhistory.TxHistoryItem
import jp.co.soramitsu.xnetworking.basic.txhistory.TxHistoryItemNested
import jp.co.soramitsu.xnetworking.basic.txhistory.TxHistoryItemParam
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.jsonPrimitive

object SoraWalletSubQueryHistoryMapper {
    fun map(response: SoraSubQueryResponse, myAddress: String): TxHistoryInfo {
        return TxHistoryInfo(
            endCursor = response.data.historyElements.pageInfo.endCursor,
            endReached = response.data.historyElements.pageInfo.hasNextPage.not(),
            items = response.data.historyElements.nodes.map {
                TxHistoryItem(
                    id = it.id,
                    blockHash = it.blockHash,
                    module = it.module,
                    method = it.method,
                    timestamp = it.timestamp,
                    networkFee = it.networkFee,
                    success = it.execution.success,
                    data = mapDataParams(it, myAddress)?.let(::mapParams),
                    nestedData = (it.data as? JsonArray)?.let { jsonArray ->
                        jsonArray.filterIsInstance<JsonObject>().map { jsonObject ->
                            TxHistoryItemNested(
                                hash = (jsonObject["hash"] as? JsonPrimitive)?.content.orEmpty(),
                                module = (jsonObject["module"] as? JsonPrimitive)?.content.orEmpty(),
                                method = (jsonObject["method"] as? JsonPrimitive)?.content.orEmpty(),
                                data = ((jsonObject["data"] as? JsonObject)?.get("args") as? JsonObject)?.let(
                                    ::mapParams
                                ) ?: emptyList()
                            )
                        }
                    },
                )
            },
        )
    }

    private fun mapParams(map: Map<String, JsonElement>): List<TxHistoryItemParam> =
        map.map { mapItem ->
            TxHistoryItemParam(
                paramName = mapItem.key,
                paramValue = (mapItem.value as? JsonPrimitive)?.content.orEmpty()
            )
        }

    private fun mapDataParams(
        item: HistoryResponseItem,
        myAddress: String,
    ): Map<String, JsonElement>? =
        when {
            item.module.equals("liquidityProxy", true) &&
                    item.method.equals("swapTransferBatch", true) -> {
                buildMap {
                    (item.data as? JsonObject)?.forEach { entry ->
                        when {
                            entry.key.equals("adarfee", true) -> this[entry.key] = entry.value
                            entry.key.equals("actualfee", true) -> this[entry.key] = entry.value
                            entry.key.equals("transfers", true) -> {
                                (entry.value as? JsonArray)?.forEach { el ->
                                    (el as? JsonObject)?.takeIf { jo ->
                                        jo.values.map { it.jsonPrimitive.content }
                                            .contains(myAddress)
                                    }?.forEach { ent ->
                                        this[ent.key] = ent.value
                                    }
                                }
                            }
                        }
                    }
                }
            }

            else -> {
                item.data as? JsonObject
            }
        }
}
