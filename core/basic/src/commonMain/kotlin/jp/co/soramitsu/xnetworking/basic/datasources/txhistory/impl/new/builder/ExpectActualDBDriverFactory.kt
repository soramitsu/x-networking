package jp.co.soramitsu.xnetworking.basic.datasources.txhistory.impl.new.builder

import com.squareup.sqldelight.db.SqlDriver

expect class ExpectActualDBDriverFactory {
    internal fun createDriver(): SqlDriver
}