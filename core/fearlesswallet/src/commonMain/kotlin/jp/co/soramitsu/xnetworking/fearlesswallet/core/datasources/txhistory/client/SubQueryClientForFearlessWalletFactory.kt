package jp.co.soramitsu.xnetworking.fearlesswallet.core.datasources.txhistory.client

import jp.co.soramitsu.xnetworking.basic.networkclient.SoramitsuNetworkClient

expect class SubQueryClientForFearlessWalletFactory {

    fun create(
        soramitsuNetworkClient: SoramitsuNetworkClient,
        pageSize: Int,
    ): SubQueryClientForFearlessWallet
}