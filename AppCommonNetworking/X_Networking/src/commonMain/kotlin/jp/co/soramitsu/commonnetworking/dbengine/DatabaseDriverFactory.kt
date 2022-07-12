package jp.co.soramitsu.commonnetworking.dbengine

import com.squareup.sqldelight.db.SqlDriver

expect class DatabaseDriverFactory {
    fun createDriver(): SqlDriver
}

class HistoryDatabaseProvider(private val historyDatabaseFactory: DatabaseDriverFactory) {
    internal fun provide(): HistoryDatabase {
        return HistoryDatabase(historyDatabaseFactory)
    }
}
