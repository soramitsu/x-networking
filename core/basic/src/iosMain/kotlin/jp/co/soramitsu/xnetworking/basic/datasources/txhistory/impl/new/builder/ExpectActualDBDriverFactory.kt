package jp.co.soramitsu.xnetworking.basic.datasources.txhistory.impl.new.builder

import com.squareup.sqldelight.db.SqlDriver
import com.squareup.sqldelight.drivers.native.NativeSqliteDriver
import jp.co.soramitsu.xnetworking.db.SoraHistoryDatabase

actual class ExpectActualDBDriverFactory(
    private val name: String
) {

    internal actual fun createDriver(): SqlDriver {
        return NativeSqliteDriver(
            schema = SoraHistoryDatabase.Schema,
            name = name
        )
    }

}