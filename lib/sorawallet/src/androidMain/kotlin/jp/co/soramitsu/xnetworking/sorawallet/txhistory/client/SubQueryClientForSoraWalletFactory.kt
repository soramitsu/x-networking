package jp.co.soramitsu.xnetworking.sorawallet.txhistory.client

import android.content.Context
import jp.co.soramitsu.xnetworking.basic.dbengine.DatabaseDriverFactory
import jp.co.soramitsu.xnetworking.basic.networkclient.SoramitsuNetworkClient
import jp.co.soramitsu.xnetworking.basic.txhistory.HistoryDatabaseProvider
import jp.co.soramitsu.xnetworking.sorawallet.mainconfig.SoraRemoteConfigBuilder

actual class SubQueryClientForSoraWalletFactory(
    private val context: Context
) {
    actual fun create(
        soramitsuNetworkClient: SoramitsuNetworkClient,
        pageSize: Int,
        soraRemoteConfigBuilder: SoraRemoteConfigBuilder,
    ): SubQueryClientForSoraWallet {
        return SubQueryClientForSoraWallet(
            networkClient = soramitsuNetworkClient,
            pageSize = pageSize,
            historyDatabaseProvider = HistoryDatabaseProvider(DatabaseDriverFactory(context)),
            soraRemoteConfigBuilder = soraRemoteConfigBuilder,
        )
    }
}