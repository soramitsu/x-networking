package jp.co.soramitsu.xnetworking.lib.engines.preferences.impl.builder

import com.russhwolf.settings.NSUserDefaultsSettings
import com.russhwolf.settings.Settings

actual class ExpectActualKeyValuePreferencesEngineFactory(
    private val preferencesName: String
) {
    internal actual fun createEngine(): Settings {
        return NSUserDefaultsSettings.Factory().create(preferencesName)
    }

}