package jp.co.soramitsu.xnetworking.sorawallet.blockexplorerinfo.sbapy

import io.ktor.http.HttpMethod
import jp.co.soramitsu.xnetworking.basic.common.Utils.toDoubleNan
import jp.co.soramitsu.xnetworking.basic.networkclient.SoramitsuNetworkClient
import jp.co.soramitsu.xnetworking.basic.common.BlockExplorerGraphQlRequest
import jp.co.soramitsu.xnetworking.sorawallet.blockexplorerinfo.BasicCases
import jp.co.soramitsu.xnetworking.sorawallet.blockexplorerinfo.sbapy.case2.SoraWalletSbApyCase2Response
import jp.co.soramitsu.xnetworking.sorawallet.blockexplorerinfo.sbapy.case2.graphQLRequestSoraWalletSbApyCase2

internal interface SoraWalletSbApyCase {

    suspend fun getSbApy(url: String, networkClient: SoramitsuNetworkClient): List<SbApyInfo>
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
    ): List<SbApyInfo> {
        var cursor = ""
        val list = mutableListOf<SbApyInfo>()
        while (true) {
            val response = networkClient.createJsonRequest<SoraWalletSbApyCase2Response>(
                url,
                HttpMethod.Post,
                BlockExplorerGraphQlRequest(graphQLRequestSoraWalletSbApyCase2(cursor)),
            )
            response.data.entities.nodes.forEach { node ->
                list.add(SbApyInfo(id = node.id, sbApy = node.strategicBonusApy?.toDoubleNan()))
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
