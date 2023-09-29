package jp.co.soramitsu.xnetworking.sorawallet.core.datasources.polkaswapconfig.impl

import io.ktor.client.call.body
import jp.co.soramitsu.xnetworking.basic.engines.preferences.api.KeyValuePreferences
import jp.co.soramitsu.xnetworking.basic.engines.rest.api.RestClient
import jp.co.soramitsu.xnetworking.sorawallet.core.datasources.polkaswapconfig.api.ConfigRepository
import jp.co.soramitsu.xnetworking.sorawallet.core.datasources.polkaswapconfig.api.models.common.CommonConfig
import jp.co.soramitsu.xnetworking.sorawallet.core.datasources.polkaswapconfig.api.models.mobile.MobileConfig
import jp.co.soramitsu.xnetworking.sorawallet.core.datasources.polkaswapconfig.impl.models.InternalGetRequest

class ConfigRepositoryImpl(
    private val commonConfigRequestUrl: String,
    private val mobileConfigRequestUrl: String,
    private val restClient: RestClient,
    private val keyValuePreferences: KeyValuePreferences
): ConfigRepository {

    override suspend fun getCommonConfig(): CommonConfig? {
        return try {
            restClient.get(
                request = InternalGetRequest(
                    requestUrl = commonConfigRequestUrl
                )
            ).body<CommonConfig>().also {
                keyValuePreferences.putSerializable(
                    serializer = CommonConfig.serializer(),
                    field = COMMON_CONFIG_KEY,
                    value = it
                )
            }
        } catch (t: Throwable) {
            keyValuePreferences.getSerializable(
                serializer = CommonConfig.serializer(),
                field = COMMON_CONFIG_KEY
            )
        }
    }

    override suspend fun getMobileConfig(): MobileConfig? {
        return try {
            restClient.get(
                request = InternalGetRequest(
                    requestUrl = mobileConfigRequestUrl
                )
            ).body<MobileConfig>().also {
                keyValuePreferences.putSerializable(
                    serializer = MobileConfig.serializer(),
                    field = MOBILE_CONFIG_KEY,
                    value = it
                )
            }
        } catch (t: Throwable) {
            keyValuePreferences.getSerializable(
                serializer = MobileConfig.serializer(),
                field = MOBILE_CONFIG_KEY
            )
        }
    }

    private companion object {
        const val COMMON_CONFIG_KEY = "COMMON_CONFIG_KEY"
        const val MOBILE_CONFIG_KEY = "MOBILE_CONFIG_KEY"
    }

}