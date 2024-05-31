package jp.co.soramitsu.xnetworking.fearlesswallet.chainbuilder

open class ChainBuilderException(message: String, cause: Throwable?) : Throwable(message, cause)

class VersionNotFoundException(message: String, cause: Throwable?) : ChainBuilderException(message, cause)
