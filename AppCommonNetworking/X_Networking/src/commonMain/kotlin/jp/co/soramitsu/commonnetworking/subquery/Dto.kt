package jp.co.soramitsu.commonnetworking.subquery

data class SbApyInfo(
    val tokenId: String,
    val priceUsd: Double? = null,
    val sbApy: Double? = null,
)

data class ReferrerRewardsInfo(
    val rewards: List<ReferrerReward>,
)

data class ReferrerReward(
    val referral: String,
    val amount: String,
)
