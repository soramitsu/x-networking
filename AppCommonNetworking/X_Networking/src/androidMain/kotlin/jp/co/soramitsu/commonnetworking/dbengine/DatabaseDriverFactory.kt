package jp.co.soramitsu.commonnetworking.dbengine

import android.content.Context
import com.squareup.sqldelight.android.AndroidSqliteDriver
import com.squareup.sqldelight.db.SqlDriver
import jp.co.soramitsu.commonnetworking.db.SoraHistoryDatabase

actual class DatabaseDriverFactory(private val context: Context) {
    actual fun createDriver(): SqlDriver {
        return AndroidSqliteDriver(SoraHistoryDatabase.Schema, context, "historyDatabase.db")
    }
}
