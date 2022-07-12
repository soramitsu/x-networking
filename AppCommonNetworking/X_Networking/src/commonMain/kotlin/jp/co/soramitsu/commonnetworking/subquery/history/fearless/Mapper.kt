package jp.co.soramitsu.commonnetworking.subquery.history.fearless

import jp.co.soramitsu.commonnetworking.subquery.history.SubQueryHistoryInfo
import jp.co.soramitsu.commonnetworking.subquery.history.SubQueryHistoryItem
import jp.co.soramitsu.commonnetworking.subquery.history.SubQueryHistoryItemParam

internal object FearlessMapper {
    fun map(response: FearlessSubQueryResponse): SubQueryHistoryInfo {
        return SubQueryHistoryInfo(
            endReached = response.data.historyElements.pageInfo.hasNextPage.not(),
            endCursor = response.data.historyElements.pageInfo.endCursor,
            items = response.data.historyElements.nodes.map {
                SubQueryHistoryItem(
                    id = it.id,
                    blockHash = "",
                    module = if (it.reward != null) "reward" else if (it.transfer != null) "transfer" else if (it.extrinsic != null) "extrinsic" else "",
                    method = "",
                    timestamp = it.timestamp,
                    networkFee = "",
                    success = true,
                    nestedData = null,
                    data = if (it.reward != null) listOf(
                        SubQueryHistoryItemParam(
                            "amount",
                            it.reward.amount
                        ),
                        SubQueryHistoryItemParam(
                            "era",
                            it.reward.era.toString()
                        ),
                        SubQueryHistoryItemParam(
                            "isReward",
                            it.reward.isReward.toString()
                        ),
                        SubQueryHistoryItemParam(
                            "validator",
                            it.reward.validator
                        ),
                    ) else if (it.transfer != null) listOf(
                        SubQueryHistoryItemParam(
                            "block",
                            it.transfer.block.orEmpty()
                        ),
                        SubQueryHistoryItemParam(
                            "amount",
                            it.transfer.amount
                        ),
                        SubQueryHistoryItemParam(
                            "fee",
                            it.transfer.fee
                        ),
                        SubQueryHistoryItemParam(
                            "to",
                            it.transfer.to
                        ),
                        SubQueryHistoryItemParam(
                            "from",
                            it.transfer.from
                        ),
                        SubQueryHistoryItemParam(
                            "extrinsicHash",
                            it.transfer.extrinsicHash.orEmpty()
                        ),
                        SubQueryHistoryItemParam(
                            "success",
                            it.transfer.success.toString()
                        ),
                    ) else if (it.extrinsic != null) listOf(
                        SubQueryHistoryItemParam(
                            "call",
                            it.extrinsic.call
                        ),
                        SubQueryHistoryItemParam(
                            "fee",
                            it.extrinsic.fee
                        ),
                        SubQueryHistoryItemParam(
                            "hash",
                            it.extrinsic.hash
                        ),
                        SubQueryHistoryItemParam(
                            "module",
                            it.extrinsic.module
                        ),
                        SubQueryHistoryItemParam(
                            "success",
                            it.extrinsic.success.toString()
                        ),
                    ) else null
                )
            }
        )
    }
}