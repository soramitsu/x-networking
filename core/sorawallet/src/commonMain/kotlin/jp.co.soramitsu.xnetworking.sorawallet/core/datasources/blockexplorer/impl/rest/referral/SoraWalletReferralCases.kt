package jp.co.soramitsu.xnetworking.sorawallet.core.datasources.blockexplorer.impl.rest.referral

import io.ktor.http.HttpMethod
import jp.co.soramitsu.xnetworking.basic.networkclient.SoramitsuNetworkClient
import jp.co.soramitsu.xnetworking.basic.txhistory.subquery.graphqlrequest.SubQueryRequest
import jp.co.soramitsu.xnetworking.sorawallet.core.datasources.blockexplorer.api.models.ReferrerRewardResponse
import jp.co.soramitsu.xnetworking.sorawallet.core.datasources.blockexplorer.impl.rest.BasicCases
import jp.co.soramitsu.xnetworking.sorawallet.core.datasources.blockexplorer.impl.rest.referral.case1.SoraWalletReferrerCase1Response
import jp.co.soramitsu.xnetworking.sorawallet.core.datasources.blockexplorer.impl.rest.referral.case1.graphQLRequestSoraWalletReferrerCase1

internal interface SoraWalletReferralCase {
    suspend fun getReferrerInfo(
        url: String,
        address: String,
        networkClient: SoramitsuNetworkClient,
    ): List<ReferrerRewardResponse>
}

internal object SoraWalletReferralCases : BasicCases<SoraWalletReferralCase>() {

    override fun provideInstance(caseName: String): SoraWalletReferralCase {
        return when (caseName) {
            "1" -> SoraWalletReferralCase1()
            else -> throw IllegalArgumentException("SoraWalletReferralCases [$caseName] not found")
        }
    }
}

private class SoraWalletReferralCase1 : SoraWalletReferralCase {

    override suspend fun getReferrerInfo(
        url: String,
        address: String,
        networkClient: SoramitsuNetworkClient,
    ): List<ReferrerRewardResponse> {
        var cursor = ""
        val list = mutableListOf<ReferrerRewardResponse>()
        while (true) {
            val response = networkClient.createJsonRequest<SoraWalletReferrerCase1Response>(
                url,
                HttpMethod.Post,
                SubQueryRequest(graphQLRequestSoraWalletReferrerCase1(address, cursor))
            )
            response.data.referrerRewards.nodes.forEach {
                list.add(
                    ReferrerRewardResponse(
                        it.referral,
                        it.amount
                    )
                )
            }
            if (response.data.referrerRewards.pageInfo.hasNextPage && response.data.referrerRewards.pageInfo.endCursor != null) {
                cursor = response.data.referrerRewards.pageInfo.endCursor!!
            } else {
                break
            }
        }
        return list
    }
}
