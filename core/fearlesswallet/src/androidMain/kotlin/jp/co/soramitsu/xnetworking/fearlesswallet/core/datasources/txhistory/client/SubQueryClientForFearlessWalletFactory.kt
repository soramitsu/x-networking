package jp.co.soramitsu.xnetworking.fearlesswallet.core.datasources.txhistory.client

import android.content.Context
import jp.co.soramitsu.xnetworking.basic.datasources.txhistory.impl.old.HistoryDatabaseProvider
import jp.co.soramitsu.xnetworking.basic.dbengine.DatabaseDriverFactory
import jp.co.soramitsu.xnetworking.basic.networkclient.SoramitsuNetworkClient

actual class SubQueryClientForFearlessWalletFactory(
    private val context: Context,
) {
    actual fun create(
        soramitsuNetworkClient: SoramitsuNetworkClient,
        pageSize: Int,
    ): SubQueryClientForFearlessWallet {
        return SubQueryClientForFearlessWallet(
            networkClient = soramitsuNetworkClient,
            pageSize = pageSize,
            historyDatabaseProvider = HistoryDatabaseProvider(DatabaseDriverFactory(context)),
        )
    }
}