package jp.co.soramitsu.xnetworking.basic.engines.rest.impl.builder

import io.ktor.client.HttpClient
import jp.co.soramitsu.xnetworking.basic.engines.rest.api.models.AbstractRestClientConfig

internal fun interface HttpClientBuilder {

    fun build(restClientConfig: AbstractRestClientConfig): HttpClient

}