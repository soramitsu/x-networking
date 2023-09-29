package jp.co.soramitsu.xnetworking.sorawallet.core.datasources.blockexplorer.impl.rest

import jp.co.soramitsu.xnetworking.basic.networkclient.SoramitsuNetworkClient
import jp.co.soramitsu.xnetworking.sorawallet.core.datasources.blockexplorer.api.BlockExplorerRepository
import jp.co.soramitsu.xnetworking.sorawallet.core.datasources.blockexplorer.api.models.AssetsInfoResponse
import jp.co.soramitsu.xnetworking.sorawallet.core.datasources.blockexplorer.api.models.FiatDataResponse
import jp.co.soramitsu.xnetworking.sorawallet.core.datasources.blockexplorer.api.models.ReferrerRewardResponse
import jp.co.soramitsu.xnetworking.sorawallet.core.datasources.blockexplorer.api.models.SbApyInfoResponse
import jp.co.soramitsu.xnetworking.sorawallet.core.datasources.blockexplorer.impl.rest.assets.SoraWalletAssetsCases
import jp.co.soramitsu.xnetworking.sorawallet.core.datasources.blockexplorer.impl.rest.fiat.SoraWalletFiatCases
import jp.co.soramitsu.xnetworking.sorawallet.core.datasources.blockexplorer.impl.rest.referral.SoraWalletReferralCases
import jp.co.soramitsu.xnetworking.sorawallet.core.datasources.blockexplorer.impl.rest.sbapy.SoraWalletSbApyCases
import jp.co.soramitsu.xnetworking.sorawallet.core.datasources.mainconfig.SoraRemoteConfigBuilder

class SoraWalletBlockExplorerInfo(
    private val networkClient: SoramitsuNetworkClient,
    private val soraRemoteConfigBuilder: SoraRemoteConfigBuilder,
): BlockExplorerRepository {

    override suspend fun getFiat(requestType: String): List<FiatDataResponse> {
        val config = soraRemoteConfigBuilder.getConfig() ?: return emptyList()
        val case = SoraWalletFiatCases.getCase(config.blockExplorerType.fiat)
        return case.getFiat(config.blockExplorerUrl, networkClient)
    }

    override suspend fun getSbApyInfo(requestType: String): List<SbApyInfoResponse> {
        val config = soraRemoteConfigBuilder.getConfig() ?: return emptyList()
        val case = SoraWalletSbApyCases.getCase(config.blockExplorerType.sbapy)
        return case.getSbApy(config.blockExplorerUrl, networkClient)
    }

    override suspend fun getAssetsInfo(requestType: String, tokenIds: List<String>, timeStamp: Int): List<AssetsInfoResponse> {
        val config = soraRemoteConfigBuilder.getConfig() ?: return emptyList()
        val case = SoraWalletAssetsCases.getCase(config.blockExplorerType.assets)
        return case.getAssetsInfo(config.blockExplorerUrl, networkClient, tokenIds, timeStamp.toString())
    }

    override suspend fun getReferrerRewards(
        requestType: String,
        address: String,
    ): List<ReferrerRewardResponse> {
        val config = soraRemoteConfigBuilder.getConfig() ?: return emptyList()
        val case = SoraWalletReferralCases.getCase(config.blockExplorerType.reward)
        return case.getReferrerInfo(config.blockExplorerUrl, address, networkClient)
    }
}
