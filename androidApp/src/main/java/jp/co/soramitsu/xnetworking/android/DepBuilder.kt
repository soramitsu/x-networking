package jp.co.soramitsu.xnetworking.android

import android.content.Context
import jp.co.soramitsu.xnetworking.lib.datasources.blockexplorer.api.BlockExplorerRepository
import jp.co.soramitsu.xnetworking.lib.datasources.blockexplorer.impl.BlockExplorerRepositoryImpl
import jp.co.soramitsu.xnetworking.lib.datasources.chainsconfig.api.ConfigDAO
import jp.co.soramitsu.xnetworking.lib.datasources.chainsconfig.impl.SuperWalletConfigDAOImpl
import jp.co.soramitsu.xnetworking.lib.datasources.chainsconfig.impl.data.RemoteConfigParserImpl
import jp.co.soramitsu.xnetworking.lib.datasources.txhistory.api.HistoryItemsFilter
import jp.co.soramitsu.xnetworking.lib.datasources.txhistory.api.TxHistoryRepository
import jp.co.soramitsu.xnetworking.lib.datasources.txhistory.api.adapters.HistoryInfoRemoteLoader
import jp.co.soramitsu.xnetworking.lib.datasources.txhistory.api.models.TxHistoryItem
import jp.co.soramitsu.xnetworking.lib.datasources.txhistory.impl.TxHistoryRepositoryImpl
import jp.co.soramitsu.xnetworking.lib.datasources.txhistory.impl.builder.ExpectActualDBDriverFactory
import jp.co.soramitsu.xnetworking.lib.datasources.txhistory.impl.domain.adapters.HistoryInfoRemoteLoaderFacade
import jp.co.soramitsu.xnetworking.lib.engines.rest.api.RestClient
import jp.co.soramitsu.xnetworking.lib.engines.rest.api.models.AbstractRestClientConfig
import jp.co.soramitsu.xnetworking.lib.engines.rest.impl.RestClientImpl
import kotlinx.serialization.json.Json

object DepBuilder {

    val restClient: RestClient = RestClientImpl(
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

    private val configDAO: ConfigDAO = SuperWalletConfigDAOImpl(
        configParser = RemoteConfigParserImpl(
            restClient = restClient,
            chainsRequestUrl = "https://raw.githubusercontent.com/soramitsu/shared-features-utils/MWR-819/chains/xn.json",
        )
    )

    val historyRemoteLoaderFacade: HistoryInfoRemoteLoader = HistoryInfoRemoteLoaderFacade(
        configDAO = configDAO,
        restClient = restClient,
    )

    lateinit var txHistoryRepository: TxHistoryRepository
    lateinit var blockExplorerRepository: BlockExplorerRepository

    fun createHistoryRepo(c: Context) {
        txHistoryRepository = TxHistoryRepositoryImpl(
            historyItemsFilter = object : HistoryItemsFilter {
                override fun List<TxHistoryItem>.filterCachedHistoryItems(): List<TxHistoryItem> {
                    return this
                }

                override fun List<TxHistoryItem>.filterPagedHistoryItems(): List<TxHistoryItem> {
                    return this
                }
            },
            restClient = restClient,
            configDAO = configDAO,
            databaseDriverFactory = ExpectActualDBDriverFactory(
                context = c,
                name = "foxxx db",
            ),
        )
        blockExplorerRepository = BlockExplorerRepositoryImpl(
            configDAO = configDAO,
            restClient = restClient,
            txHistoryRepository = txHistoryRepository,
        )
    }
}
