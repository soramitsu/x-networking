package jp.co.soramitsu.xnetworking.lib.datasources.blockexplorer.api.models

data class Unbonding(
    val amount: String,
    val timestamp: String,
    val type: DelegationAction?
) {
    enum class DelegationAction {
        STAKE,
        UNSTAKE,
        REWARD,
        DELEGATE,
        OTHER;
    }
}