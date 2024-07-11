package jp.co.soramitsu.xnetworking.lib.datasources.blockexplorer.impl.domain.referralreward.sora

import jp.co.soramitsu.xnetworking.lib.datasources.blockexplorer.api.adapters.ReferralRewardFetcher
import jp.co.soramitsu.xnetworking.lib.datasources.blockexplorer.api.models.ReferralReward
import jp.co.soramitsu.xnetworking.lib.datasources.chainsconfig.api.ConfigDAO
import jp.co.soramitsu.xnetworking.lib.engines.rest.api.RestClient
import jp.co.soramitsu.xnetworking.lib.engines.utils.wrapToGraphQLString

class SoraReferralRewardsFetcher(
    private val restClient: RestClient,
    private val configDAO: ConfigDAO
): ReferralRewardFetcher() {

    override suspend fun fetch(
        chainId: String,
        address: String
    ): List<ReferralReward> {
        val result = mutableListOf<ReferralReward>()

        var cursor = ""

        while (true) {
            val response = restClient.post(
                request = SoraReferralRewardsRequest(
                    url = configDAO.historyUrl(chainId),
                    pageCount = 100,
                    cursor = cursor.wrapToGraphQLString(),
                    address = address.wrapToGraphQLString()
                )
            ).data.entities

            response.nodes.filterNotNull().forEach { node ->
                node.mapReferrerRewardsResponse()?.let { result.add(it) }
            }

            val (hasNextPage, endCursor) = response.pageInfo.run {
                (hasNextPage ?: false) to endCursor
            }

            if (!hasNextPage || endCursor == null)
                break

            cursor = endCursor
        }

        return result
    }

    private fun SoraReferralRewardsResponse.Entities.Node.mapReferrerRewardsResponse(): ReferralReward? {
        return ReferralReward(
            referral = referral ?: return null,
            amount = amount.toString()
        )
    }

}