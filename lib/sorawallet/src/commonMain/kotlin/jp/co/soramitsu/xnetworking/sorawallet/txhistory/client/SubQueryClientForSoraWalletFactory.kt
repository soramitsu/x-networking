package jp.co.soramitsu.xnetworking.sorawallet.txhistory.client

import jp.co.soramitsu.xnetworking.basic.networkclient.SoramitsuNetworkClient
import jp.co.soramitsu.xnetworking.sorawallet.mainconfig.SoraRemoteConfigBuilder

expect class SubQueryClientForSoraWalletFactory {

    fun create(
        soramitsuNetworkClient: SoramitsuNetworkClient,
        pageSize: Int,
        soraRemoteConfigBuilder: SoraRemoteConfigBuilder,
    ): SubQueryClientForSoraWallet
}