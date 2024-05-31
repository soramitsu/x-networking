package jp.co.soramitsu.xnetworking.lib.datasources.txhistory.api.utils

abstract class PackedCursor {

    abstract operator fun set(key: String, cursor: String?)

    abstract operator fun get(key: String): String?

    abstract fun pack(): String?

    companion object;

}