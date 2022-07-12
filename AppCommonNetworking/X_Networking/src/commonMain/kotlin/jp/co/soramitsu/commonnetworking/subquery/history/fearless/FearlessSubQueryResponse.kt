package jp.co.soramitsu.commonnetworking.subquery.history.fearless

import kotlinx.serialization.Serializable

@Serializable
data class FearlessSubQueryResponse(
    val data: FearlessSubQueryResponseData
)

@Serializable
data class FearlessSubQueryResponseData(
    val historyElements: FearlessHistoryResponseDataElements
)

@Serializable
data class FearlessHistoryResponseDataElements(
    val nodes: List<FearlessHistoryResponseItem>, val pageInfo: FearlessHistoryResponsePageInfo
)

@Serializable
data class FearlessHistoryResponsePageInfo(
    val endCursor: String?,
    val hasNextPage: Boolean,
)

@Serializable
data class FearlessHistoryResponseItem(
    val id: String,
    val timestamp: String,
    val address: String,
    val reward: FearlessRewardItem?,
    val transfer: FearlessTransferItem?,
    val extrinsic: FearlessExtrinsicItem?,
)

@Serializable
data class FearlessRewardItem(
    val era: Int,
    val amount: String,
    val isReward: Boolean,
    val validator: String,
)

@Serializable
data class FearlessTransferItem(
    val amount: String,
    val to: String,
    val from: String,
    val fee: String,
    val block: String? = null,
    val success: Boolean,
    val extrinsicHash: String? = null
)

@Serializable
data class FearlessExtrinsicItem(
    val hash: String,
    val module: String,
    val call: String,
    val fee: String,
    val success: Boolean
)
