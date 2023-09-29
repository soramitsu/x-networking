package jp.co.soramitsu.xnetworking.sorawallet.core.datasources.polkaswapconfig.api.models.sora

import jp.co.soramitsu.xnetworking.basic.common.platform
import jp.co.soramitsu.xnetworking.basic.common.platform_android
import jp.co.soramitsu.xnetworking.sorawallet.core.datasources.polkaswapconfig.api.models.common.CommonConfig
import jp.co.soramitsu.xnetworking.sorawallet.core.datasources.polkaswapconfig.api.models.mobile.MobileConfig

class SoraConfig private constructor(
    commonConfig: CommonConfig,
    mobileConfig: MobileConfig
) {

    companion object {

        fun create(commonConfig: CommonConfig?, mobileConfig: MobileConfig?): SoraConfig? {
            if (commonConfig == null || mobileConfig == null)
                return null

            return SoraConfig(commonConfig, mobileConfig)
        }

    }

    val blockExplorerUrl: String = commonConfig.subquery

    val blockExplorerType: ConfigExplorerType = ConfigExplorerType(
        assets = mobileConfig.explorerTypeAssets,
        fiat = mobileConfig.explorerTypeFiat,
        reward = mobileConfig.explorerTypeReward,
        sbapy = mobileConfig.explorerTypeSbapy
    )

    val nodes: List<SoraConfigNodeInfo> = commonConfig.nodes.map {
        SoraConfigNodeInfo(
            chain = it.chain,
            name = it.name,
            address = it.address
        )
    }

    val genesis: String = commonConfig.genesis

    val joinUrl: String = mobileConfig.joinLink

    val substrateTypesUrl: String = if (platform() == platform_android)
        mobileConfig.substrateTypesAndroid else mobileConfig.substrateTypesIos

    val soracard: Boolean = mobileConfig.soracard

    val currencies: List<SoraConfigCurrencyInfo> = mobileConfig.currencies.map {
        SoraConfigCurrencyInfo(
            name = it.name,
            code = it.code,
            sign = it.sign
        )
    }

}

