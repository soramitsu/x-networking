package jp.co.soramitsu.xnetworking.lib.datasources.txhistory.impl.utils

internal object Utils {
    fun String.toDoubleNan(): Double? = this.toDoubleOrNull()?.let {
        if (it.isNaN()) null else it
    }
}
