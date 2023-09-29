package jp.co.soramitsu.xnetworking.sorawallet.core.datasources.mainconfig

internal const val configName = "remote_config"

expect class SoraRemoteConfigProvider {
    fun provide(): SoraRemoteConfigBuilder
}