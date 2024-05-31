package jp.co.soramitsu.xnetworking.android

import jp.co.soramitsu.xnetworking.lib.datasources.blockexplorer.impl.BlockExplorerRepositoryImpl
import jp.co.soramitsu.xnetworking.lib.datasources.blockexplorer.impl.domain.assetinfo.sora.SoraAssetInfoFetcher
import jp.co.soramitsu.xnetworking.lib.datasources.blockexplorer.impl.domain.fiat.sora.SoraFiatFetcher
import jp.co.soramitsu.xnetworking.lib.datasources.blockexplorer.impl.domain.referralreward.sora.SoraReferralRewardsFetcher
import jp.co.soramitsu.xnetworking.lib.datasources.blockexplorer.impl.domain.apy.ApyFetcherFacade
import jp.co.soramitsu.xnetworking.lib.datasources.blockexplorer.impl.domain.apy.adapters.sora.SoraApyFetcher
import jp.co.soramitsu.xnetworking.lib.datasources.blockexplorer.impl.domain.apy.adapters.subquery.SubQueryApyFetcher
import jp.co.soramitsu.xnetworking.lib.datasources.blockexplorer.impl.domain.apy.adapters.subquid.SubSquidApyFetcher
import jp.co.soramitsu.xnetworking.lib.datasources.blockexplorer.impl.domain.assetinfo.AssetInfoFetcherFacade
import jp.co.soramitsu.xnetworking.lib.datasources.blockexplorer.impl.domain.fiat.FiatFetcherFacade
import jp.co.soramitsu.xnetworking.lib.datasources.blockexplorer.impl.domain.referralreward.ReferralRewardFetcherFacade
import jp.co.soramitsu.xnetworking.lib.datasources.blockexplorer.impl.domain.unbonding.UnbondingFetcherFacade
import jp.co.soramitsu.xnetworking.lib.datasources.blockexplorer.impl.domain.unbonding.adapters.subquery.SubQueryUnbondingFetcher
import jp.co.soramitsu.xnetworking.lib.datasources.blockexplorer.impl.domain.unbonding.adapters.subsquid.SubSquidUnbondingFetcher
import jp.co.soramitsu.xnetworking.lib.datasources.blockexplorer.impl.domain.validators.ValidatorsFetcherFacade
import jp.co.soramitsu.xnetworking.lib.datasources.blockexplorer.impl.domain.validators.adapters.sora.SoraValidatorsFetcher
import jp.co.soramitsu.xnetworking.lib.datasources.blockexplorer.impl.domain.validators.adapters.subquery.SubQueryValidatorsFetcher
import jp.co.soramitsu.xnetworking.lib.datasources.blockexplorer.impl.domain.validators.adapters.subsquid.SubSquidValidatorsFetcher
import jp.co.soramitsu.xnetworking.lib.datasources.chainsconfig.api.ConfigDAO
import jp.co.soramitsu.xnetworking.lib.datasources.chainsconfig.api.models.ExternalApiType
import jp.co.soramitsu.xnetworking.lib.datasources.chainsconfig.impl.data.InMemorySavingConfigFetcherImpl
import jp.co.soramitsu.xnetworking.lib.datasources.chainsconfig.impl.FearlessConfigDAOImpl
import jp.co.soramitsu.xnetworking.lib.datasources.txhistory.api.adapters.HistoryInfoRemoteLoader
import jp.co.soramitsu.xnetworking.lib.datasources.txhistory.impl.domain.adapters.HistoryInfoRemoteLoaderFacade
import jp.co.soramitsu.xnetworking.lib.datasources.txhistory.impl.domain.adapters.etherscan.EtherScanHistoryInfoRemoteLoader
import jp.co.soramitsu.xnetworking.lib.datasources.txhistory.impl.domain.adapters.giantsquid.GiantSquidHistoryInfoRemoteLoader
import jp.co.soramitsu.xnetworking.lib.datasources.txhistory.impl.domain.adapters.oklink.OkLinkHistoryInfoRemoteLoader
import jp.co.soramitsu.xnetworking.lib.datasources.txhistory.impl.domain.adapters.reef.ReefHistoryInfoRemoteLoader
import jp.co.soramitsu.xnetworking.lib.datasources.txhistory.impl.domain.adapters.sorasubquery.SoraSubQueryHistoryInfoRemoteLoader
import jp.co.soramitsu.xnetworking.lib.datasources.txhistory.impl.domain.adapters.subquery.SubQueryHistoryInfoRemoteLoader
import jp.co.soramitsu.xnetworking.lib.datasources.txhistory.impl.domain.adapters.subsquid.SubSquidHistoryInfoRemoteLoader
import jp.co.soramitsu.xnetworking.lib.datasources.txhistory.impl.domain.adapters.zeta.ZetaHistoryInfoRemoteLoader
import jp.co.soramitsu.xnetworking.lib.engines.apollo.api.ApolloClientStore
import jp.co.soramitsu.xnetworking.lib.engines.apollo.impl.ApolloClientStoreImpl
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

    private val apolloClientStore: ApolloClientStore = ApolloClientStoreImpl()

    private val configDAO: ConfigDAO = FearlessConfigDAOImpl(
        configFetcher = InMemorySavingConfigFetcherImpl(
            restClient = restClient,
            chainsRequestUrl = "https://raw.githubusercontent.com/soramitsu/shared-features-utils/develop-free/chains/v9/chains_dev.json"
        )
    )

    val historyRemoteLoaderFacade: HistoryInfoRemoteLoader = HistoryInfoRemoteLoaderFacade(
        configDAO = configDAO,
        loaders = mapOf(
            ExternalApiType.ETHERSCAN to EtherScanHistoryInfoRemoteLoader(
                configDAO = configDAO,
                restClient = restClient,
                apiKeys = mapOf(
                    ChainInfoConstants.EtherScan.chainInfo.chainId to "paste your EtherScan key"
                )
            ),
            ExternalApiType.GIANTSQUID to GiantSquidHistoryInfoRemoteLoader(
                configDAO = configDAO,
                restClient = restClient
            ),
            ExternalApiType.OKLINK to OkLinkHistoryInfoRemoteLoader(
                configDAO = configDAO,
                restClient = restClient,
                apiKeys = mapOf(
                    ChainInfoConstants.OkLink.chainInfo.chainId to "paste your OkLink key"
                )
            ),
            ExternalApiType.REEF to ReefHistoryInfoRemoteLoader(
                configDAO = configDAO,
                restClient = restClient
            ),
            ExternalApiType.SORA to SoraSubQueryHistoryInfoRemoteLoader(
                configDAO = configDAO,
                apolloClientStore = apolloClientStore
            ),
            ExternalApiType.SUBSQUID to SubSquidHistoryInfoRemoteLoader(
                configDAO = configDAO,
                restClient = restClient
            ),
            ExternalApiType.SUBQUERY to SubQueryHistoryInfoRemoteLoader(
                configDAO = configDAO,
                restClient = restClient
            ),
            ExternalApiType.ZETA to ZetaHistoryInfoRemoteLoader(
                configDAO = configDAO,
                restClient = restClient
            )
        )
    )

    val blockExplorerRepository = BlockExplorerRepositoryImpl(
        assetInfoFetcher = AssetInfoFetcherFacade(
            configDAO = configDAO,
            fetchersMap = mapOf(
                ExternalApiType.SORA to SoraAssetInfoFetcher(
                    configDAO = configDAO,
                    apolloClientStore = apolloClientStore
                )
            )
        ),
        apyFetcher = ApyFetcherFacade(
            configDAO = configDAO,
            fetchersMap = mapOf(
                ExternalApiType.SORA to SoraApyFetcher(
                    configDAO = configDAO,
                    apolloClientStore = apolloClientStore
                ),
                ExternalApiType.SUBQUERY to SubQueryApyFetcher(
                    configDAO = configDAO,
                    restClient = restClient
                ),
                ExternalApiType.SUBSQUID to SubSquidApyFetcher(
                    configDAO = configDAO,
                    restClient = restClient
                )
            )
        ),
        fiatFetcher = FiatFetcherFacade(
            configDAO = configDAO,
            fetchersMap = mapOf(
                ExternalApiType.SORA to SoraFiatFetcher(
                    configDAO = configDAO,
                    apolloClientStore = apolloClientStore
                )
            )
        ),
        referralRewardFetcher = ReferralRewardFetcherFacade(
            configDAO = configDAO,
            fetchersMap = mapOf(
                ExternalApiType.SORA to SoraReferralRewardsFetcher(
                    configDAO = configDAO,
                    apolloClientStore = apolloClientStore
                )
            )
        ),
        unbondingFetcher = UnbondingFetcherFacade(
            configDAO = configDAO,
            fetchersMap = mapOf(
                ExternalApiType.SUBQUERY to SubQueryUnbondingFetcher(
                    configDAO = configDAO,
                    restClient = restClient
                ),
                ExternalApiType.SUBSQUID to SubSquidUnbondingFetcher(
                    configDAO = configDAO,
                    restClient = restClient
                )
            )
        ),
        validatorsFetcher = ValidatorsFetcherFacade(
            configDAO = configDAO,
            fetchersMap = mapOf(
                ExternalApiType.SORA to SoraValidatorsFetcher(
                    configDAO = configDAO,
                    restClient = restClient
                ),
                ExternalApiType.SUBQUERY to SubQueryValidatorsFetcher(
                    configDAO = configDAO,
                    restClient = restClient
                ),
                ExternalApiType.SUBSQUID to SubSquidValidatorsFetcher()
            )
        )
    )
}
