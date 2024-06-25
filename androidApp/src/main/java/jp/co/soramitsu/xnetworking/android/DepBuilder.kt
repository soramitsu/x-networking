package jp.co.soramitsu.xnetworking.android

import jp.co.soramitsu.xnetworking.lib.datasources.blockexplorer.api.BlockExplorerRepository
import jp.co.soramitsu.xnetworking.lib.datasources.blockexplorer.impl.BlockExplorerRepositoryImpl
import jp.co.soramitsu.xnetworking.lib.datasources.chainsconfig.api.ConfigDAO
import jp.co.soramitsu.xnetworking.lib.datasources.chainsconfig.impl.data.RemoteConfigParserImpl
import jp.co.soramitsu.xnetworking.lib.datasources.chainsconfig.impl.SuperWalletConfigDAOImpl
import jp.co.soramitsu.xnetworking.lib.datasources.txhistory.api.adapters.HistoryInfoRemoteLoader
import jp.co.soramitsu.xnetworking.lib.datasources.txhistory.impl.domain.adapters.HistoryInfoRemoteLoaderFacade
import jp.co.soramitsu.xnetworking.lib.engines.rest.api.RestClient
import jp.co.soramitsu.xnetworking.lib.engines.rest.api.models.AbstractRestClientConfig
import jp.co.soramitsu.xnetworking.lib.engines.rest.impl.RestClientImpl
import kotlinx.serialization.json.Json

object DepBuilder {

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

    private val configDAO: ConfigDAO = SuperWalletConfigDAOImpl(
        configParser = RemoteConfigParserImpl(
            restClient = restClient,
            chainsRequestUrl = "https://raw.githubusercontent.com/soramitsu/shared-features-utils/develop-free/chains/v9/chains_dev.json"
        )
    )

    val historyRemoteLoaderFacade: HistoryInfoRemoteLoader = HistoryInfoRemoteLoaderFacade(
        configDAO = configDAO,
        restClient = restClient,
        etherScanApiKeys = mapOf(
            ChainInfoConstants.EtherScan.chainInfo.chainId to "paste your EtherScan key"
        ),
        oklinkApiKeys = mapOf(
            ChainInfoConstants.OkLink.chainInfo.chainId to "paste your OkLink key"
        )
    )

    val blockExplorerRepository: BlockExplorerRepository = BlockExplorerRepositoryImpl(
        configDAO = configDAO,
        restClient = restClient
    )
}
