package jp.co.soramitsu.xnetworking.lib.engines.rest.api.models

import jp.co.soramitsu.xnetworking.lib.engines.rest.api.RestClient
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.Transient
import kotlin.experimental.ExperimentalObjCRefinement
import kotlin.native.HiddenFromObjC

abstract class AbstractRestServerRequest<T> {

    open val bearerToken: String? = null

    open val headers: Map<String, String>? = null

    open val userAgent: String? = null

    open val queryParams: Map<String, String>? = null

    open val responseContentType: RestClient.ContentType = RestClient.ContentType.JSON

    abstract val url: String

    @OptIn(ExperimentalObjCRefinement::class)
    @HiddenFromObjC
    @Transient
    abstract val responseDeserializer: DeserializationStrategy<T>

    /**
     * Specific Implementation of equals that ignores @param #responseDeserializer
     */
    override fun equals(other: Any?): Boolean {
        if (other !is AbstractRestServerRequest<*>)
            return false

        if (this !is WithBody && other is WithBody<*>)
            return false

        var areEqual = true

        areEqual = areEqual.and(bearerToken == other.bearerToken)

        headers?.let { thisHeaders ->
            val otherHeaders = other.headers ?: return false

            areEqual = areEqual.and(thisHeaders.keys.containsAll(otherHeaders.keys))
            areEqual = areEqual.and(thisHeaders.values.containsAll(otherHeaders.values))
        }

        areEqual = areEqual.and(userAgent == other.userAgent)

        queryParams?.let { thisQueryParams ->
            val otherQueryParams = other.queryParams ?: return false

            areEqual = areEqual.and(thisQueryParams.keys.containsAll(otherQueryParams.keys))
            areEqual = areEqual.and(thisQueryParams.values.containsAll(otherQueryParams.values))
        }

        areEqual = areEqual.and(responseContentType === other.responseContentType)
        areEqual = areEqual.and(url == other.url)

        return areEqual
    }

    override fun hashCode(): Int {
        return super.hashCode()
    }

    abstract class WithBody<Response>: AbstractRestServerRequest<Response>() {

        abstract val requestContentType: RestClient.ContentType
        
        abstract val body: Any

        override fun equals(other: Any?): Boolean {
            if (other !is WithBody<*>)
                return false

            var areEqual = super.equals(other)

            areEqual = areEqual.and(requestContentType === other.requestContentType)
            areEqual = areEqual.and(body == other.body)

            return areEqual
        }
        
    }

}