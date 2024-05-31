package jp.co.soramitsu.xnetworking.lib.engines.preferences.impl.builder

import android.content.Context
import com.russhwolf.settings.Settings
import com.russhwolf.settings.SharedPreferencesSettings

actual class ExpectActualKeyValuePreferencesEngineFactory(
    private val context: Context,
    private val preferencesName: String
) {
    internal actual fun createEngine(): Settings {
        return SharedPreferencesSettings.Factory(
            context = context
        ).create(preferencesName)
    }

}