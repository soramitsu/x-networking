package jp.co.soramitsu.xnetworking.basic.dbengine

import com.squareup.sqldelight.db.SqlDriver
import com.squareup.sqldelight.drivers.native.NativeSqliteDriver
import jp.co.soramitsu.xnetworking.db.SoraHistoryDatabase

actual class DatabaseDriverFactory {
    actual fun createDriver(): SqlDriver {
        return NativeSqliteDriver(SoraHistoryDatabase.Schema, "historyDatabase.db")
    }
}
