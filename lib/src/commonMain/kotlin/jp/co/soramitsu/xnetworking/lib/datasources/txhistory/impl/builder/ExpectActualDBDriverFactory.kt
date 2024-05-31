package jp.co.soramitsu.xnetworking.lib.datasources.txhistory.impl.builder

import com.squareup.sqldelight.db.SqlDriver

expect class ExpectActualDBDriverFactory {
    internal fun createDriver(): SqlDriver
}