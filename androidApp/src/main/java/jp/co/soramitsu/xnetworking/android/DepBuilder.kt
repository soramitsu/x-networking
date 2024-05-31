package jp.co.soramitsu.xnetworking.android

import android.content.Context
import jp.co.soramitsu.xnetworking.basic.networkclient.SoramitsuNetworkClient
import jp.co.soramitsu.xnetworking.fearlesswallet.chainbuilder.FearlessChainsBuilder
import jp.co.soramitsu.xnetworking.fearlesswallet.txhistory.client.SubQueryClientForFearlessWallet
import jp.co.soramitsu.xnetworking.fearlesswallet.txhistory.client.SubQueryClientForFearlessWalletFactory
import jp.co.soramitsu.xnetworking.sorawallet.blockexplorerinfo.SoraWalletBlockExplorerInfo
import jp.co.soramitsu.xnetworking.sorawallet.mainconfig.SoraRemoteConfigProvider
import jp.co.soramitsu.xnetworking.sorawallet.tokenwhitelist.SoraTokensWhitelistManager
import jp.co.soramitsu.xnetworking.sorawallet.txhistory.client.SubQueryClientForSoraWallet
import jp.co.soramitsu.xnetworking.sorawallet.txhistory.client.SubQueryClientForSoraWalletFactory

object DepBuilder {

    val soraNetworkClient = SoramitsuNetworkClient(logging = true, timeout = 20000)
    private val fearlessChainsBuilder = FearlessChainsBuilder(
        soraNetworkClient,
        "https://raw.githubusercontent.com/arvifox/arvifoxandroid/develop/felete/",
        "chains/index_android.json"
    )

    private lateinit var subQueryClientForSoraWallet: SubQueryClientForSoraWallet
    private lateinit var subQueryClientForFearlessWallet: SubQueryClientForFearlessWallet
    lateinit var networkService: NetworkService
    private const val pageSize = 30

    fun build(ctx: Context) {
        val soraRemoteConfigBuilder = SoraRemoteConfigProvider(
            ctx,
            soraNetworkClient,
            "https://config.polkaswap2.io/stage/common.json",
            "https://config.polkaswap2.io/stage/mobile.json",
        ).provide()
        subQueryClientForSoraWallet =
            SubQueryClientForSoraWalletFactory(
                ctx
            ).create(
                soraNetworkClient,
                pageSize,
                soraRemoteConfigBuilder,
            )
        subQueryClientForFearlessWallet =
            SubQueryClientForFearlessWalletFactory(
                ctx
            ).create(
                soraNetworkClient,
                pageSize,
            )
        networkService = NetworkService(
            soraNetworkClient,
            fearlessChainsBuilder,
            soraRemoteConfigBuilder,
            subQueryClientForFearlessWallet,
            subQueryClientForSoraWallet,
            SoraWalletBlockExplorerInfo(
                soraNetworkClient,
                soraRemoteConfigBuilder,
            ),
            SoraTokensWhitelistManager(
                soraNetworkClient
            ),
        )
    }
}
