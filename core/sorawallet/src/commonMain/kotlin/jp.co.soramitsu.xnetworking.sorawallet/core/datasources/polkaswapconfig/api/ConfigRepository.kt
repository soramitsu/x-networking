package jp.co.soramitsu.xnetworking.sorawallet.core.datasources.polkaswapconfig.api

import jp.co.soramitsu.xnetworking.sorawallet.core.datasources.polkaswapconfig.api.models.common.CommonConfig
import jp.co.soramitsu.xnetworking.sorawallet.core.datasources.polkaswapconfig.api.models.mobile.MobileConfig

interface ConfigRepository {

    suspend fun getCommonConfig(): CommonConfig?

    suspend fun getMobileConfig(): MobileConfig?

}