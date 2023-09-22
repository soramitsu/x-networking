package jp.co.soramitsu.xnetworking.basic.datasources.txhistory.impl.new.builder

import android.content.Context
import com.squareup.sqldelight.android.AndroidSqliteDriver
import com.squareup.sqldelight.db.SqlDriver
import jp.co.soramitsu.xnetworking.db.SoraHistoryDatabase

actual class ExpectActualDBDriverFactory(
    private val context: Context,
    private val name: String
) {

    internal actual fun createDriver(): SqlDriver {
        return AndroidSqliteDriver(
            schema = SoraHistoryDatabase.Schema,
            context = context,
            name = name
        )
    }

}