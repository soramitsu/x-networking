package jp.co.soramitsu.xnetworking.sorawallet.blockexplorerinfo.referral

data class ReferrerRewardsInfo(
    val rewards: List<ReferrerReward>,
)

data class ReferrerReward(
    val referral: String,
    val amount: String,
)
