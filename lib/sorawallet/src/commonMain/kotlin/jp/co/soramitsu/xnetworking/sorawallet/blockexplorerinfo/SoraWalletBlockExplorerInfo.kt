package jp.co.soramitsu.xnetworking.sorawallet.blockexplorerinfo

import jp.co.soramitsu.xnetworking.basic.networkclient.SoramitsuNetworkClient
import jp.co.soramitsu.xnetworking.basic.networkclient.SoramitsuNetworkException
import jp.co.soramitsu.xnetworking.sorawallet.blockexplorerinfo.assets.AssetsInfo
import jp.co.soramitsu.xnetworking.sorawallet.blockexplorerinfo.assets.SoraWalletAssetsCases
import jp.co.soramitsu.xnetworking.sorawallet.blockexplorerinfo.fiat.FiatData
import jp.co.soramitsu.xnetworking.sorawallet.blockexplorerinfo.fiat.SoraWalletFiatCases
import jp.co.soramitsu.xnetworking.sorawallet.blockexplorerinfo.referral.ReferrerRewardsInfo
import jp.co.soramitsu.xnetworking.sorawallet.blockexplorerinfo.referral.SoraWalletReferralCases
import jp.co.soramitsu.xnetworking.sorawallet.blockexplorerinfo.sbapy.SbApyInfo
import jp.co.soramitsu.xnetworking.sorawallet.blockexplorerinfo.sbapy.SoraWalletSbApyCases
import jp.co.soramitsu.xnetworking.sorawallet.mainconfig.SoraRemoteConfigBuilder
import kotlin.coroutines.cancellation.CancellationException

class SoraWalletBlockExplorerInfo(
    private val networkClient: SoramitsuNetworkClient,
    private val soraRemoteConfigBuilder: SoraRemoteConfigBuilder,
) {

    @Throws(
        SoramitsuNetworkException::class,
        CancellationException::class,
        IllegalArgumentException::class
    )
    suspend fun getFiat(): List<FiatData> {
        val config = soraRemoteConfigBuilder.getConfig() ?: return emptyList()
        val case = SoraWalletFiatCases.getCase(config.blockExplorerType.fiat)
        return case.getFiat(config.blockExplorerUrl, networkClient)
    }

    @Throws(
        SoramitsuNetworkException::class,
        CancellationException::class,
        IllegalArgumentException::class
    )
    suspend fun getSpApy(): List<SbApyInfo> {
        val config = soraRemoteConfigBuilder.getConfig() ?: return emptyList()
        val case = SoraWalletSbApyCases.getCase(config.blockExplorerType.sbapy)
        return case.getSbApy(config.blockExplorerUrl, networkClient)
    }

    @Throws(
        SoramitsuNetworkException::class,
        CancellationException::class,
        IllegalArgumentException::class
    )
    suspend fun getAssetsInfo(tokenIds: List<String>, timestamp: Long): List<AssetsInfo> {
        val config = soraRemoteConfigBuilder.getConfig() ?: return emptyList()
        val case = SoraWalletAssetsCases.getCase(config.blockExplorerType.assets)
        return case.getAssetsInfo(config.blockExplorerUrl, networkClient, tokenIds, timestamp)
    }

    @Throws(
        SoramitsuNetworkException::class,
        CancellationException::class,
        IllegalArgumentException::class
    )
    suspend fun getReferrerRewards(
        address: String,
    ): ReferrerRewardsInfo {
        val config = soraRemoteConfigBuilder.getConfig() ?: return ReferrerRewardsInfo(emptyList())
        val case = SoraWalletReferralCases.getCase(config.blockExplorerType.reward)
        return case.getReferrerInfo(config.blockExplorerUrl, address, networkClient)
    }
}
