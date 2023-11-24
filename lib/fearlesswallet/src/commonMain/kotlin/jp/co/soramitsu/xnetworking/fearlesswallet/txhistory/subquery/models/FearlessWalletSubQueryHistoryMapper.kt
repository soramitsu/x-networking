package jp.co.soramitsu.xnetworking.fearlesswallet.txhistory.subquery.models

import jp.co.soramitsu.xnetworking.basic.txhistory.TxHistoryInfo
import jp.co.soramitsu.xnetworking.basic.txhistory.TxHistoryItem
import jp.co.soramitsu.xnetworking.basic.txhistory.TxHistoryItemNested
import jp.co.soramitsu.xnetworking.basic.txhistory.TxHistoryItemParam
import jp.co.soramitsu.xnetworking.fearlesswallet.txhistory.subsquid.models.FearlessSubSquidResponse
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive

internal object FearlessWalletSubQueryHistoryMapper {
    fun map(response: FearlessSubQueryResponse): TxHistoryInfo {
        return TxHistoryInfo(
            endReached = response.data.historyElements.pageInfo.hasNextPage.not(),
            endCursor = response.data.historyElements.pageInfo.endCursor,
            items = response.data.historyElements.nodes.map {
                TxHistoryItem(
                    id = it.id,
                    blockHash = "",
                    module = if (it.reward != null) "reward" else if (it.transfer != null) "transfer" else if (it.extrinsic != null) "extrinsic" else "",
                    method = "",
                    timestamp = it.timestamp,
                    networkFee = "",
                    success = true,
                    nestedData = null,
                    data = if (it.reward != null) listOf(
                        TxHistoryItemParam(
                            "amount",
                            it.reward.amount
                        ),
                        TxHistoryItemParam(
                            "era",
                            it.reward.era.toString()
                        ),
                        TxHistoryItemParam(
                            "isReward",
                            it.reward.isReward.toString()
                        ),
                        TxHistoryItemParam(
                            "validator",
                            it.reward.validator
                        ),
                    ) else if (it.transfer != null) listOf(
                        TxHistoryItemParam(
                            "block",
                            it.transfer.block.orEmpty()
                        ),
                        TxHistoryItemParam(
                            "amount",
                            it.transfer.amount
                        ),
                        TxHistoryItemParam(
                            "fee",
                            it.transfer.fee
                        ),
                        TxHistoryItemParam(
                            "to",
                            it.transfer.to
                        ),
                        TxHistoryItemParam(
                            "from",
                            it.transfer.from
                        ),
                        TxHistoryItemParam(
                            "extrinsicHash",
                            it.transfer.extrinsicHash.orEmpty()
                        ),
                        TxHistoryItemParam(
                            "success",
                            it.transfer.success.toString()
                        ),
                    ) else if (it.extrinsic != null) listOf(
                        TxHistoryItemParam(
                            "call",
                            it.extrinsic.call
                        ),
                        TxHistoryItemParam(
                            "fee",
                            it.extrinsic.fee
                        ),
                        TxHistoryItemParam(
                            "hash",
                            it.extrinsic.hash
                        ),
                        TxHistoryItemParam(
                            "module",
                            it.extrinsic.module
                        ),
                        TxHistoryItemParam(
                            "success",
                            it.extrinsic.success.toString()
                        ),
                    ) else null
                )
            }
        )
    }
}