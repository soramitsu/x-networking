package jp.co.soramitsu.appxnetworking

import android.content.Context
import jp.co.soramitsu.xnetworking.basic.datasources.txhistory.api.HistoryItemsFilter
import jp.co.soramitsu.xnetworking.basic.datasources.txhistory.api.models.TxHistoryItem
import jp.co.soramitsu.xnetworking.basic.datasources.txhistory.impl.new.TxHistoryRepositoryImpl
import jp.co.soramitsu.xnetworking.basic.datasources.txhistory.impl.new.builder.ExpectActualDBDriverFactory
import jp.co.soramitsu.xnetworking.basic.engines.apollo.api.ApolloClientStore
import jp.co.soramitsu.xnetworking.basic.engines.apollo.impl.ApolloClientStoreImpl
import jp.co.soramitsu.xnetworking.basic.engines.preferences.api.KeyValuePreferences
import jp.co.soramitsu.xnetworking.basic.engines.preferences.impl.builder.ExpectActualKeyValuePreferencesEngineFactory
import jp.co.soramitsu.xnetworking.basic.engines.preferences.impl.KeyValuePreferencesImpl
import jp.co.soramitsu.xnetworking.basic.engines.rest.api.RestClient
import jp.co.soramitsu.xnetworking.basic.engines.rest.api.models.AbstractRestClientConfig
import jp.co.soramitsu.xnetworking.basic.engines.rest.impl.RestClientImpl
import jp.co.soramitsu.xnetworking.basic.networkclient.SoramitsuNetworkClient
import jp.co.soramitsu.xnetworking.fearlesswallet.common.interactors.txhistory.impl.TxHistoryInteractorImpl as FearlessTxHistoryInteractorImpl
import jp.co.soramitsu.xnetworking.fearlesswallet.core.datasources.chainbuilder.FearlessChainsBuilder
import jp.co.soramitsu.xnetworking.sorawallet.common.interactors.blockexplorer.impl.BlockExplorerInteractorImpl
import jp.co.soramitsu.xnetworking.sorawallet.core.datasources.blockexplorer.impl.graphql.GraphQLBlockExplorerRepositoryImpl
import jp.co.soramitsu.xnetworking.sorawallet.core.datasources.mainconfig.SoraRemoteConfigProvider
import jp.co.soramitsu.xnetworking.fearlesswallet.common.interactors.txhistory.impl.FearlessHistoryInfoRemoteLoader
import jp.co.soramitsu.xnetworking.sorawallet.core.datasources.polkaswapconfig.impl.ConfigRepositoryCachingDecorator
import jp.co.soramitsu.xnetworking.sorawallet.core.datasources.polkaswapconfig.impl.ConfigRepositoryImpl
import jp.co.soramitsu.xnetworking.sorawallet.core.datasources.polkaswapwhitelist.impl.WhitelistRepositoryImpl
import kotlinx.serialization.json.Json

object DepBuilder {

    lateinit var networkService: NetworkService

    private val soraNetworkClient = SoramitsuNetworkClient(logging = true)

    private val fearlessChainsBuilder = FearlessChainsBuilder(
        soraNetworkClient,
        "https://raw.githubusercontent.com/arvifox/arvifoxandroid/develop/felete/",
        "chains/index_android.json"
    )

    private val restClient: RestClient = RestClientImpl(
        restClientConfig = object : AbstractRestClientConfig() {

            override fun isLoggingEnabled(): Boolean = true

            override fun getConnectTimeoutMillis(): Long = 30_000L

            override fun getRequestTimeoutMillis(): Long = 30_000L

            override fun getSocketTimeoutMillis(): Long = 30_000L

            override fun getOrCreateJsonConfig(): Json = Json {
                prettyPrint = true
                isLenient = true
                ignoreUnknownKeys = true
            }

        }
    )

    fun build(ctx: Context) {
        val apolloClientStore: ApolloClientStore = ApolloClientStoreImpl()

        val keyValuePreferences: KeyValuePreferences = KeyValuePreferencesImpl(
            keyValuePreferencesEngine = ExpectActualKeyValuePreferencesEngineFactory(
                context = ctx,
                preferencesName = "Test"
            )
        )

        val configRepository = ConfigRepositoryCachingDecorator(
            configRepository = ConfigRepositoryImpl(
                commonConfigRequestUrl = "https://config.polkaswap2.io/dev/common.json",
                mobileConfigRequestUrl = "https://config.polkaswap2.io/dev/mobile.json",
                restClient = restClient,
                keyValuePreferences = keyValuePreferences
            )
        )

        val soraRemoteConfigBuilder = SoraRemoteConfigProvider(
            ctx,
            soraNetworkClient,
            "https://config.polkaswap2.io/dev/common.json",
            "https://config.polkaswap2.io/dev/mobile.json",
        ).provide()

        networkService = NetworkService(
            fearlessChainsBuilder = fearlessChainsBuilder,
            soraConfigBuilder = soraRemoteConfigBuilder,
            restClient = restClient,
            blockExplorerInteractor = BlockExplorerInteractorImpl(
                apolloClientStore = apolloClientStore,
                configRepository = configRepository,
                blockExplorerRepository = GraphQLBlockExplorerRepositoryImpl(
                    apolloClientStore = apolloClientStore
                ),
            ),
            whitelistRepository = WhitelistRepositoryImpl(
                restClient = restClient
            ),
            txHistoryInteractor = FearlessTxHistoryInteractorImpl(
                apolloClientStore = apolloClientStore,
                txHistoryRepository = TxHistoryRepositoryImpl(
                    databaseDriverFactory = ExpectActualDBDriverFactory(
                        context = ctx,
                        name = "historyDatabase.db"
                    ),
                    historyInfoRemoteLoader = FearlessHistoryInfoRemoteLoader(
                        apolloClientStore
                    ),
                    historyItemsFilter = object : HistoryItemsFilter {
                        override fun List<TxHistoryItem>.filterCachedHistoryItems(): List<TxHistoryItem> {
                            return this
                        }

                        override fun List<TxHistoryItem>.filterPagedHistoryItems(): List<TxHistoryItem> {
                            return this
                        }
                    }
                )
            )
        )
    }
}
