package jp.co.soramitsu.xnetworking.sorawallet.core.datasources.mainconfig

import android.content.Context
import com.russhwolf.settings.Settings
import com.russhwolf.settings.SharedPreferencesSettings
import jp.co.soramitsu.xnetworking.basic.networkclient.SoramitsuNetworkClient

actual class SoraRemoteConfigProvider(
    private val context: Context,
    private val client: SoramitsuNetworkClient,
    private val commonUrl: String,
    private val mobileUrl: String,
) {
    actual fun provide(): SoraRemoteConfigBuilder {
        val factory: Settings.Factory = SharedPreferencesSettings.Factory(context)
        val settings = factory.create(configName)
        return SoraRemoteConfigBuilder(
            client,
            commonUrl,
            mobileUrl,
            settings,
        )
    }
}