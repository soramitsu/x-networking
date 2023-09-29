package jp.co.soramitsu.appxnetworking

import android.content.Context
import com.russhwolf.settings.SharedPreferencesSettings
import jp.co.soramitsu.xnetworking.basic.engines.apollo.api.ApolloClientStore
import jp.co.soramitsu.xnetworking.basic.engines.apollo.impl.ApolloClientStoreImpl
import jp.co.soramitsu.xnetworking.basic.engines.preferences.api.KeyValuePreferences
import jp.co.soramitsu.xnetworking.basic.engines.preferences.impl.KeyValuePreferencesImpl
import jp.co.soramitsu.xnetworking.basic.engines.rest.api.RestClient
import jp.co.soramitsu.xnetworking.basic.engines.rest.api.models.AbstractRestClientConfig
import jp.co.soramitsu.xnetworking.basic.engines.rest.impl.RestClientImpl
import jp.co.soramitsu.xnetworking.basic.networkclient.SoramitsuNetworkClient
import jp.co.soramitsu.xnetworking.fearlesswallet.chainbuilder.FearlessChainsBuilder
import jp.co.soramitsu.xnetworking.fearlesswallet.txhistory.client.SubQueryClientForFearlessWallet
import jp.co.soramitsu.xnetworking.fearlesswallet.txhistory.client.SubQueryClientForFearlessWalletFactory
import jp.co.soramitsu.xnetworking.sorawallet.common.interactors.blockexplorer.impl.BlockExplorerInteractorImpl
import jp.co.soramitsu.xnetworking.sorawallet.core.datasources.blockexplorer.impl.graphql.GraphQLBlockExplorerRepositoryImpl
import jp.co.soramitsu.xnetworking.sorawallet.core.datasources.blockexplorer.impl.rest.SoraWalletBlockExplorerInfo
import jp.co.soramitsu.xnetworking.sorawallet.core.datasources.mainconfig.SoraRemoteConfigProvider
import jp.co.soramitsu.xnetworking.sorawallet.core.datasources.polkaswapconfig.impl.ConfigRepositoryCachingDecorator
import jp.co.soramitsu.xnetworking.sorawallet.core.datasources.polkaswapconfig.impl.ConfigRepositoryImpl
import jp.co.soramitsu.xnetworking.sorawallet.core.datasources.polkaswapwhitelist.impl.WhitelistRepositoryImpl
import jp.co.soramitsu.xnetworking.sorawallet.core.datasources.txhistory.client.SubQueryClientForSoraWallet
import jp.co.soramitsu.xnetworking.sorawallet.core.datasources.txhistory.client.SubQueryClientForSoraWalletFactory
import kotlinx.serialization.json.Json

object DepBuilder {

    private val soraNetworkClient = SoramitsuNetworkClient(logging = true)

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

    private val apolloClientStore: ApolloClientStore = ApolloClientStoreImpl()

    private val fearlessChainsBuilder = FearlessChainsBuilder(
        soraNetworkClient,
        "https://raw.githubusercontent.com/arvifox/arvifoxandroid/develop/felete/",
        "chains/index_android.json"
    )

    lateinit var subQueryClientForSoraWallet: SubQueryClientForSoraWallet
    lateinit var subQueryClientForFearlessWallet: SubQueryClientForFearlessWallet
    lateinit var networkService: NetworkService

    lateinit var soraBlockExplorer: SoraWalletBlockExplorerInfo

    fun build(ctx: Context) {
        val keyValuePreferences: KeyValuePreferences = KeyValuePreferencesImpl(
            settings = SharedPreferencesSettings.Factory(
                context = ctx
            ).create("Test")
        )
        val soraRemoteConfigBuilder = SoraRemoteConfigProvider(
            ctx,
            soraNetworkClient,
            "https://config.polkaswap2.io/dev/common.json",
            "https://config.polkaswap2.io/dev/mobile.json",
        ).provide()
        soraBlockExplorer = SoraWalletBlockExplorerInfo(
            networkClient = soraNetworkClient,
            soraRemoteConfigBuilder = soraRemoteConfigBuilder
        )
        subQueryClientForSoraWallet =
            SubQueryClientForSoraWalletFactory(
                ctx
            ).create(
                soraNetworkClient,
                30,
                soraRemoteConfigBuilder,
            )
        subQueryClientForFearlessWallet =
            SubQueryClientForFearlessWalletFactory(
                ctx
            ).create(
                soraNetworkClient,
                30,
            )
        networkService = NetworkService(
            client = soraNetworkClient,
            fearlessChainsBuilder = fearlessChainsBuilder,
            soraConfigBuilder = soraRemoteConfigBuilder,
            subQueryClientForFearlessWallet = subQueryClientForFearlessWallet,
            subQueryClientForSoraWallet = subQueryClientForSoraWallet,
            restClient = restClient,
            blockExplorerInteractor = BlockExplorerInteractorImpl(
                apolloClientStore = apolloClientStore,
                configRepository = ConfigRepositoryCachingDecorator(
                    configRepository = ConfigRepositoryImpl(
                        commonConfigRequestUrl = "https://config.polkaswap2.io/dev/common.json",
                        mobileConfigRequestUrl = "https://config.polkaswap2.io/dev/mobile.json",
                        restClient = restClient,
                        keyValuePreferences = keyValuePreferences
                    )
                ),
                blockExplorerRepository = GraphQLBlockExplorerRepositoryImpl(
                    apolloClientStore = apolloClientStore
                ),
            ),
            whitelistRepository = WhitelistRepositoryImpl(
                restClient = restClient
            )
        )
    }
}
