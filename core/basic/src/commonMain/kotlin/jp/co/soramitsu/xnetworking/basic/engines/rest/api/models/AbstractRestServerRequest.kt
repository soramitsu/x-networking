package jp.co.soramitsu.xnetworking.basic.engines.rest.api.models

import jp.co.soramitsu.xnetworking.basic.engines.rest.api.RestClient

abstract class AbstractRestServerRequest {

    open fun getBearerToken(): String? = null

    open fun getHeaders(): Map<String, String>? = null 

    open fun getUserAgent(): String? = null

    abstract fun getUrl(): String

    abstract fun getResponseContentType(): RestClient.ContentType

    abstract class WithBody: AbstractRestServerRequest() {

        abstract fun getRequestBodyContentType(): RestClient.ContentType
        
        abstract fun getBody(): Any
        
    }

}