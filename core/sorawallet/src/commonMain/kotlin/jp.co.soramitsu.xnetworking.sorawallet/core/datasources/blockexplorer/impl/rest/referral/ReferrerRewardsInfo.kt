package jp.co.soramitsu.xnetworking.sorawallet.core.datasources.blockexplorer.impl.rest.referral

data class ReferrerRewardsInfo(
    val rewards: List<ReferrerReward>,
)

data class ReferrerReward(
    val referral: String,
    val amount: String,
)
