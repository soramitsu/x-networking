package jp.co.soramitsu.xnetworking.fearlesswallet.txhistory.client

import android.content.Context
import jp.co.soramitsu.xnetworking.basic.dbengine.DatabaseDriverFactory
import jp.co.soramitsu.xnetworking.basic.networkclient.SoramitsuNetworkClient
import jp.co.soramitsu.xnetworking.basic.txhistory.HistoryDatabaseProvider

actual class TxHistoryClientForFearlessWalletFactory(
    private val context: Context,
) {
    actual fun createSubQuery(
        soramitsuNetworkClient: SoramitsuNetworkClient,
        pageSize: Int,
    ): SubQueryClientForFearlessWallet {
        return SubQueryClientForFearlessWallet(
            networkClient = soramitsuNetworkClient,
            pageSize = pageSize,
            historyDatabaseProvider = HistoryDatabaseProvider(DatabaseDriverFactory(context)),
        )
    }

    actual fun createSubSquid(
        soramitsuNetworkClient: SoramitsuNetworkClient,
        pageSize: Int
    ): SubSquidClientForFearlessWallet {
        return SubSquidClientForFearlessWallet(
            networkClient = soramitsuNetworkClient,
            pageSize = pageSize,
            historyDatabaseProvider = HistoryDatabaseProvider(DatabaseDriverFactory(context)),
        )
    }
}