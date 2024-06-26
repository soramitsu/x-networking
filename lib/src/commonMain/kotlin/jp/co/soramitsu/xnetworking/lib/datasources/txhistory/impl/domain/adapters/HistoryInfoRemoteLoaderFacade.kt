package jp.co.soramitsu.xnetworking.lib.datasources.txhistory.impl.domain.adapters

import jp.co.soramitsu.xnetworking.lib.datasources.chainsconfig.api.ConfigDAO
import jp.co.soramitsu.xnetworking.lib.datasources.chainsconfig.api.models.ExternalApiType
import jp.co.soramitsu.xnetworking.lib.datasources.txhistory.api.models.ChainInfo
import jp.co.soramitsu.xnetworking.lib.datasources.txhistory.api.adapters.HistoryInfoRemoteLoader
import jp.co.soramitsu.xnetworking.lib.datasources.txhistory.api.models.TxFilter
import jp.co.soramitsu.xnetworking.lib.datasources.txhistory.api.models.TxHistoryInfo
import jp.co.soramitsu.xnetworking.lib.datasources.txhistory.impl.domain.adapters.etherscan.EtherScanHistoryInfoRemoteLoader
import jp.co.soramitsu.xnetworking.lib.datasources.txhistory.impl.domain.adapters.giantsquid.GiantSquidHistoryInfoRemoteLoader
import jp.co.soramitsu.xnetworking.lib.datasources.txhistory.impl.domain.adapters.oklink.OkLinkHistoryInfoRemoteLoader
import jp.co.soramitsu.xnetworking.lib.datasources.txhistory.impl.domain.adapters.reef.ReefHistoryInfoRemoteLoader
import jp.co.soramitsu.xnetworking.lib.datasources.txhistory.impl.domain.adapters.sorasubquery.SoraSubQueryHistoryInfoRemoteLoader
import jp.co.soramitsu.xnetworking.lib.datasources.txhistory.impl.domain.adapters.sorasubsquid.SoraSubSquidHistoryInfoRemoteLoader
import jp.co.soramitsu.xnetworking.lib.datasources.txhistory.impl.domain.adapters.subquery.SubQueryHistoryInfoRemoteLoader
import jp.co.soramitsu.xnetworking.lib.datasources.txhistory.impl.domain.adapters.subsquid.SubSquidHistoryInfoRemoteLoader
import jp.co.soramitsu.xnetworking.lib.datasources.txhistory.impl.domain.adapters.zeta.ZetaHistoryInfoRemoteLoader
import jp.co.soramitsu.xnetworking.lib.engines.apollo.api.ApolloClientStore
import jp.co.soramitsu.xnetworking.lib.engines.apollo.impl.ApolloClientStoreImpl
import jp.co.soramitsu.xnetworking.lib.engines.rest.api.RestClient
import jp.co.soramitsu.xnetworking.lib.engines.utils.CachingFactory

class HistoryInfoRemoteLoaderFacade(
    private val configDAO: ConfigDAO,
    private val apolloClientStore: ApolloClientStore,
    private val restClient: RestClient
): HistoryInfoRemoteLoader() {
    constructor(
        configDAO: ConfigDAO,
        restClient: RestClient,
    ): this(
        configDAO = configDAO,
        apolloClientStore = ApolloClientStoreImpl(),
        restClient = restClient
    )

    private data class Args(
        val externalApiType: ExternalApiType,
        val externalApiUrl: String
    ): CachingFactory.Args()

    private val cachingFactory = CachingFactory<Args, HistoryInfoRemoteLoader> {
        if (externalApiType === ExternalApiType.EtherScan)
            return@CachingFactory EtherScanHistoryInfoRemoteLoader(configDAO, restClient)

        if (externalApiType === ExternalApiType.GiantSquid)
            return@CachingFactory GiantSquidHistoryInfoRemoteLoader(configDAO, restClient)

        if (externalApiType === ExternalApiType.OkLink)
            return@CachingFactory OkLinkHistoryInfoRemoteLoader(configDAO, restClient)

        if (externalApiType === ExternalApiType.Reef)
            return@CachingFactory ReefHistoryInfoRemoteLoader(configDAO, restClient)

        if (externalApiType === ExternalApiType.Sora) {
            if ("subsquid." in externalApiUrl) {
                return@CachingFactory SoraSubSquidHistoryInfoRemoteLoader(configDAO, restClient)
            } else {
                return@CachingFactory SoraSubQueryHistoryInfoRemoteLoader(apolloClientStore, configDAO)
            }
        }

        if (externalApiType === ExternalApiType.SubQuery)
            return@CachingFactory SubQueryHistoryInfoRemoteLoader(configDAO, restClient)

        if (externalApiType === ExternalApiType.SubSquid)
            return@CachingFactory SubSquidHistoryInfoRemoteLoader(configDAO, restClient)

        if (externalApiType === ExternalApiType.Zeta)
            return@CachingFactory ZetaHistoryInfoRemoteLoader(configDAO, restClient)

        error("Remote History Loader could not have been found.")
    }

    override suspend fun loadHistoryInfo(
        pageCount: Int,
        cursor: String?,
        signAddress: String,
        chainInfo: ChainInfo,
        filters: Set<TxFilter>
    ): TxHistoryInfo = cachingFactory.getOrCreate(
        args = Args(
            externalApiType = configDAO.historyType(chainInfo.chainId),
            externalApiUrl = configDAO.historyUrl(chainInfo.chainId)
        )
    ).loadHistoryInfo(pageCount, cursor, signAddress, chainInfo, filters)
}