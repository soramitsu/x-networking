package jp.co.soramitsu.xnetworking.basic.engines.rest.api.models

sealed class RestClientException(
    message: String,
    error: Throwable?
) : Throwable(message, error) {

    class WithCode(
        val code: Int,
        message: String,
        error: Throwable?
    ) : RestClientException(message, error)

    class WhileSerialization(
        message: String,
        error: Throwable?
    ) : RestClientException(message, error)

    class SimpleException(
        message: String,
        error: Throwable?
    ) : RestClientException(message, error)

}

fun RestClientException.parseToError() =
    when(this) {
        is RestClientException.SimpleException -> message ?: ""
        is RestClientException.WhileSerialization -> message ?: ""
        is RestClientException.WithCode -> message ?: ""
    }

