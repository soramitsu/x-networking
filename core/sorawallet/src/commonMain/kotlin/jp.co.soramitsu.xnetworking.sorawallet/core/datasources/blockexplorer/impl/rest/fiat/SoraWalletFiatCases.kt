package jp.co.soramitsu.xnetworking.sorawallet.core.datasources.blockexplorer.impl.rest.fiat

import io.ktor.http.HttpMethod
import jp.co.soramitsu.xnetworking.basic.common.Utils.toDoubleNan
import jp.co.soramitsu.xnetworking.basic.datasources.txhistory.impl.old.subquery.graphqlrequest.SubQueryRequest
import jp.co.soramitsu.xnetworking.basic.networkclient.SoramitsuNetworkClient
import jp.co.soramitsu.xnetworking.sorawallet.core.datasources.blockexplorer.api.models.FiatDataResponse
import jp.co.soramitsu.xnetworking.sorawallet.core.datasources.blockexplorer.impl.rest.BasicCases
import jp.co.soramitsu.xnetworking.sorawallet.core.datasources.blockexplorer.impl.rest.fiat.case2.SoraWalletFiatCase2Response
import jp.co.soramitsu.xnetworking.sorawallet.core.datasources.blockexplorer.impl.rest.fiat.case2.graphQLRequestSoraWalletFiatCase2

internal interface SoraWalletFiatCase {

    suspend fun getFiat(url: String, networkClient: SoramitsuNetworkClient): List<FiatDataResponse>
}

internal object SoraWalletFiatCases : BasicCases<SoraWalletFiatCase>() {
    override fun provideInstance(caseName: String): SoraWalletFiatCase {
        return when (caseName) {
            "2" -> SoraWalletFiatCase2()
            else -> throw IllegalArgumentException("SoraWalletFiatCases [$caseName] not found")
        }
    }
}

private class SoraWalletFiatCase2 : SoraWalletFiatCase {
    override suspend fun getFiat(
        url: String,
        networkClient: SoramitsuNetworkClient
    ): List<FiatDataResponse> {
        var cursor = ""
        val list = mutableListOf<FiatDataResponse>()
        while (true) {
            val response = networkClient.createJsonRequest<SoraWalletFiatCase2Response>(
                url,
                HttpMethod.Post,
                SubQueryRequest(graphQLRequestSoraWalletFiatCase2(cursor)),
            )
            response.data.entities.nodes.forEach { node ->
                list.add(
                    FiatDataResponse(
                        node.id,
                        node.priceUSD.toDoubleNan().toString()
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
