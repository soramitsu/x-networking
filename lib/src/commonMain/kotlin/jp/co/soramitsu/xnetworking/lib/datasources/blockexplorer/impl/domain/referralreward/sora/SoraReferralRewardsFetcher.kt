package jp.co.soramitsu.xnetworking.lib.datasources.blockexplorer.impl.domain.referralreward.sora

import jp.co.soramitsu.xnetworking.lib.datasources.blockexplorer.api.adapters.ReferralRewardFetcher
import jp.co.soramitsu.xnetworking.sorawallet.GetReferrerRewardsQuery
import jp.co.soramitsu.xnetworking.lib.datasources.blockexplorer.api.models.ReferralReward
import jp.co.soramitsu.xnetworking.lib.datasources.chainsconfig.api.ConfigDAO
import jp.co.soramitsu.xnetworking.lib.engines.apollo.api.ApolloClientStore

class SoraReferralRewardsFetcher(
    private val apolloClientStore: ApolloClientStore,
    private val configDAO: ConfigDAO
): ReferralRewardFetcher() {

    override suspend fun fetch(
        chainId: String,
        address: String
    ): List<ReferralReward> {
        val result = mutableListOf<ReferralReward>()

        var cursor = ""

        while (true) {
            val response = apolloClientStore.query(
                configDAO.stakingUrl(chainId),
                GetReferrerRewardsQuery(
                    pageCount = 100,
                    cursor = cursor,
                    address = address
                )
            ).entities ?: return emptyList()

            response.nodes.filterNotNull().forEach { node ->
                result.add(node.mapReferrerRewardsResponse())
            }

            val (hasNextPage, endCursor) = response.pageInfo.run {
                hasNextPage to endCursor
            }

            if (!hasNextPage || endCursor == null)
                break

            cursor = endCursor
        }

        return result
    }

    private fun GetReferrerRewardsQuery.Node.mapReferrerRewardsResponse() =
        ReferralReward(
            referral = referral,
            amount = amount.toString()
        )

}