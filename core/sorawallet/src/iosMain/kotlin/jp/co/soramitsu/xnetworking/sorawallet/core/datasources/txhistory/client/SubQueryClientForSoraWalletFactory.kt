package jp.co.soramitsu.xnetworking.sorawallet.core.datasources.txhistory.client

import jp.co.soramitsu.xnetworking.basic.datasources.txhistory.impl.old.HistoryDatabaseProvider
import jp.co.soramitsu.xnetworking.basic.dbengine.DatabaseDriverFactory
import jp.co.soramitsu.xnetworking.basic.networkclient.SoramitsuNetworkClient
import jp.co.soramitsu.xnetworking.sorawallet.core.datasources.mainconfig.SoraRemoteConfigBuilder

actual class SubQueryClientForSoraWalletFactory {
    actual fun create(
        soramitsuNetworkClient: SoramitsuNetworkClient,
        pageSize: Int,
        soraRemoteConfigBuilder: SoraRemoteConfigBuilder,
    ): SubQueryClientForSoraWallet {
        return SubQueryClientForSoraWallet(
            networkClient = soramitsuNetworkClient,
            pageSize = pageSize,
            historyDatabaseProvider = HistoryDatabaseProvider(DatabaseDriverFactory()),
            soraRemoteConfigBuilder = soraRemoteConfigBuilder,
        )
    }
}