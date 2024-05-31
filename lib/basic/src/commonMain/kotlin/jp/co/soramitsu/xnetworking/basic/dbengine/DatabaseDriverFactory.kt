package jp.co.soramitsu.xnetworking.basic.dbengine

import com.squareup.sqldelight.db.SqlDriver

expect class DatabaseDriverFactory {
    fun createDriver(): SqlDriver
}
