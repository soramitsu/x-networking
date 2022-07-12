package jp.co.soramitsu.commonnetworking.db

import com.squareup.sqldelight.Transacter
import com.squareup.sqldelight.db.SqlDriver
import jp.co.soramitsu.commonnetworking.db.XNetworking.newInstance
import jp.co.soramitsu.commonnetworking.db.XNetworking.schema

public interface SoraHistoryDatabase : Transacter {
  public val soraHistoryDatabaseQueries: SoraHistoryDatabaseQueries

  public companion object {
    public val Schema: SqlDriver.Schema
      get() = SoraHistoryDatabase::class.schema

    public operator fun invoke(driver: SqlDriver): SoraHistoryDatabase =
        SoraHistoryDatabase::class.newInstance(driver)
  }
}
