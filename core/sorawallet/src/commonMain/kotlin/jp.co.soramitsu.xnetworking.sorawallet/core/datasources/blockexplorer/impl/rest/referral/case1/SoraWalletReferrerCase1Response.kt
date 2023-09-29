package jp.co.soramitsu.xnetworking.sorawallet.core.datasources.blockexplorer.impl.rest.referral.case1

import jp.co.soramitsu.xnetworking.basic.common.ResponsePageInfo
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SoraWalletReferrerCase1Response(
    @SerialName("data")
    val data: SoraWalletReferrerCase1ResponseData
)

@Serializable
data class SoraWalletReferrerCase1ResponseData(
    @SerialName("referrerRewards")
    val referrerRewards: SoraWalletReferrerCase1ResponseDataRewards
)

@Serializable
data class SoraWalletReferrerCase1ResponseDataRewards(
    @SerialName("pageInfo")
    val pageInfo: ResponsePageInfo,
    @SerialName("nodes")
    val nodes: List<SoraWalletReferrerCase1ResponseDataRewardsNode>,
)

@Serializable
data class SoraWalletReferrerCase1ResponseDataRewardsNode(
    @SerialName("referral")
    val referral: String,
    @SerialName("amount")
    val amount: String,
)
