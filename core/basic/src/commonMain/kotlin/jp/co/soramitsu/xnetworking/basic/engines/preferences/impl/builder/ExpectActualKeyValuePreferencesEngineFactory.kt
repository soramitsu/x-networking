package jp.co.soramitsu.xnetworking.basic.engines.preferences.impl.builder

import com.russhwolf.settings.Settings

expect class ExpectActualKeyValuePreferencesEngineFactory {

    internal fun createEngine(): Settings

}