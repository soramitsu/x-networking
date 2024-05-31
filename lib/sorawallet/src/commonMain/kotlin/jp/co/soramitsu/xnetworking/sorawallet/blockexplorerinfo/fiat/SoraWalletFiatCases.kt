package jp.co.soramitsu.xnetworking.sorawallet.blockexplorerinfo.fiat

import io.ktor.http.HttpMethod
import jp.co.soramitsu.xnetworking.basic.common.Utils.toDoubleNan
import jp.co.soramitsu.xnetworking.basic.networkclient.SoramitsuNetworkClient
import jp.co.soramitsu.xnetworking.basic.txhistory.subquery.graphqlrequest.SubQueryRequest
import jp.co.soramitsu.xnetworking.sorawallet.blockexplorerinfo.BasicCases
import jp.co.soramitsu.xnetworking.sorawallet.blockexplorerinfo.fiat.case2.SoraWalletFiatCase2Response
import jp.co.soramitsu.xnetworking.sorawallet.blockexplorerinfo.fiat.case2.graphQLRequestSoraWalletFiatCase2

internal interface SoraWalletFiatCase {

    suspend fun getFiat(url: String, networkClient: SoramitsuNetworkClient): List<FiatData>
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
    ): List<FiatData> {
        var cursor = ""
        val list = mutableListOf<FiatData>()
        while (true) {
            val response = networkClient.createJsonRequest<SoraWalletFiatCase2Response>(
                url,
                HttpMethod.Post,
                SubQueryRequest(graphQLRequestSoraWalletFiatCase2(cursor)),
            )
            response.data.entities.nodes.forEach { node ->
                list.add(FiatData(node.id, node.priceUSD.toDoubleNan()))
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
