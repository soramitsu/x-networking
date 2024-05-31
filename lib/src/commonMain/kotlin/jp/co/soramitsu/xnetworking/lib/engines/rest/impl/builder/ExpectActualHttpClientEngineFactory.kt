package jp.co.soramitsu.xnetworking.lib.engines.rest.impl.builder

import io.ktor.client.engine.HttpClientEngineConfig
import io.ktor.client.engine.HttpClientEngineFactory

internal expect class ExpectActualHttpClientEngineFactory constructor() {
    fun createEngine(): HttpClientEngineFactory<HttpClientEngineConfig>
}