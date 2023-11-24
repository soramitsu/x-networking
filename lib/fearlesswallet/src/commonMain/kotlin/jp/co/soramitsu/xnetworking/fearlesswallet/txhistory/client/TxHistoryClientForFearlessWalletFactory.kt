package jp.co.soramitsu.xnetworking.fearlesswallet.txhistory.client

import jp.co.soramitsu.xnetworking.basic.networkclient.SoramitsuNetworkClient

expect class TxHistoryClientForFearlessWalletFactory {

    fun createSubQuery(
        soramitsuNetworkClient: SoramitsuNetworkClient,
        pageSize: Int,
    ): SubQueryClientForFearlessWallet

    fun createSubSquid(
        soramitsuNetworkClient: SoramitsuNetworkClient,
        pageSize: Int,
    ): SubSquidClientForFearlessWallet
}