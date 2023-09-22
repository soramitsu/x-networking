package jp.co.soramitsu.xnetworking.sorawallet.core.datasources.blockexplorer.impl.rest.sbapy

import io.ktor.http.HttpMethod
import jp.co.soramitsu.xnetworking.basic.common.Utils.toDoubleNan
import jp.co.soramitsu.xnetworking.basic.datasources.txhistory.impl.old.subquery.graphqlrequest.SubQueryRequest
import jp.co.soramitsu.xnetworking.basic.networkclient.SoramitsuNetworkClient
import jp.co.soramitsu.xnetworking.sorawallet.core.datasources.blockexplorer.api.models.SbApyInfoResponse
import jp.co.soramitsu.xnetworking.sorawallet.core.datasources.blockexplorer.impl.rest.BasicCases
import jp.co.soramitsu.xnetworking.sorawallet.core.datasources.blockexplorer.impl.rest.sbapy.case2.SoraWalletSbApyCase2Response
import jp.co.soramitsu.xnetworking.sorawallet.core.datasources.blockexplorer.impl.rest.sbapy.case2.graphQLRequestSoraWalletSbApyCase2

internal interface SoraWalletSbApyCase {

    suspend fun getSbApy(url: String, networkClient: SoramitsuNetworkClient): List<SbApyInfoResponse>
}

internal object SoraWalletSbApyCases : BasicCases<SoraWalletSbApyCase>() {

    override fun provideInstance(caseName: String): SoraWalletSbApyCase {
        return when (caseName) {
            "2" -> SoraWalletSbApyCase2()
            else -> throw IllegalArgumentException("SoraWalletSbApyCases [$caseName] not found")
        }
    }
}

private class SoraWalletSbApyCase2 : SoraWalletSbApyCase {

    override suspend fun getSbApy(
        url: String,
        networkClient: SoramitsuNetworkClient
    ): List<SbApyInfoResponse> {
        var cursor = ""
        val list = mutableListOf<SbApyInfoResponse>()
        while (true) {
            val response = networkClient.createJsonRequest<SoraWalletSbApyCase2Response>(
                url,
                HttpMethod.Post,
                SubQueryRequest(graphQLRequestSoraWalletSbApyCase2(cursor)),
            )
            response.data.entities.nodes.forEach { node ->
                list.add(
                    SbApyInfoResponse(
                        id = node.id,
                        strategicBonusApy = node.strategicBonusApy?.toDoubleNan().toString()
                    )
                )
            }
            if (response.data.entities.pageInfo.hasNextPage && response.data.entities.pageInfo.endCursor != null) {
                cursor = response.data.entities.pageInfo.endCursor!!
            } else {
                break
            }
        }
        return list
    }
}
