package jp.co.soramitsu.commonnetworking.fearless

open class ChainBuilderException(message: String, cause: Throwable?) : Throwable(message, cause)

class PlatformNotFoundException(message: String, cause: Throwable?) : ChainBuilderException(message, cause)

class VersionNotFoundException(message: String, cause: Throwable?) : ChainBuilderException(message, cause)
