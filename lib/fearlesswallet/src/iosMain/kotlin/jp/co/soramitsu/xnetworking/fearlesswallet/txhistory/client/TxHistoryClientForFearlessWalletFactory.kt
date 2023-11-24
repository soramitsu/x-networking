package jp.co.soramitsu.xnetworking.fearlesswallet.txhistory.client

import jp.co.soramitsu.xnetworking.basic.dbengine.DatabaseDriverFactory
import jp.co.soramitsu.xnetworking.basic.networkclient.SoramitsuNetworkClient
import jp.co.soramitsu.xnetworking.basic.txhistory.HistoryDatabaseProvider

actual class TxHistoryClientForFearlessWalletFactory {
    actual fun createSubQuery(
        soramitsuNetworkClient: SoramitsuNetworkClient,
        pageSize: Int,
    ): SubQueryClientForFearlessWallet {
        return SubQueryClientForFearlessWallet(
            networkClient = soramitsuNetworkClient,
            pageSize = pageSize,
            historyDatabaseProvider = HistoryDatabaseProvider(DatabaseDriverFactory()),
        )
    }

    actual fun createSubSquid(
        soramitsuNetworkClient: SoramitsuNetworkClient,
        pageSize: Int
    ): SubSquidClientForFearlessWallet {
        return SubSquidClientForFearlessWallet(
            networkClient = soramitsuNetworkClient,
            pageSize = pageSize,
            historyDatabaseProvider = HistoryDatabaseProvider(DatabaseDriverFactory())
        )
    }
}