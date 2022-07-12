package jp.co.soramitsu.commonnetworking.subquery.response

import kotlinx.serialization.Serializable

@Serializable
data class ReferrerRewardsResponse(
    val data: ReferrerRewardsResponseData
)

@Serializable
data class ReferrerRewardsResponseData(
    val referrerRewards: ReferrerRewardsData
)

@Serializable
data class ReferrerRewardsData(
    val pageInfo: ReferrerRewardsPageInfo,
    val nodes: List<ReferrerRewardsItem>,
)

@Serializable
data class ReferrerRewardsPageInfo(
    val hasNextPage: Boolean,
    val endCursor: String?,
)

@Serializable
data class ReferrerRewardsItem(
    val referral: String,
    val amount: String,
)
