package jp.co.soramitsu.commonnetworking.db.commonNetworking

import com.squareup.sqldelight.Query
import com.squareup.sqldelight.TransacterImpl
import com.squareup.sqldelight.`internal`.copyOnWriteList
import com.squareup.sqldelight.db.SqlCursor
import com.squareup.sqldelight.db.SqlDriver
import jp.co.soramitsu.commonnetworking.db.ExtrinsicParam
import jp.co.soramitsu.commonnetworking.db.Extrinsics
import jp.co.soramitsu.commonnetworking.db.SignerInfo
import jp.co.soramitsu.commonnetworking.db.SoraHistoryDatabase
import jp.co.soramitsu.commonnetworking.db.SoraHistoryDatabaseQueries
import kotlin.Any
import kotlin.Boolean
import kotlin.Int
import kotlin.Long
import kotlin.String
import kotlin.Unit
import kotlin.collections.MutableList
import kotlin.reflect.KClass

internal val KClass<SoraHistoryDatabase>.schema: SqlDriver.Schema
  get() = SoraHistoryDatabaseImpl.Schema

internal fun KClass<SoraHistoryDatabase>.newInstance(driver: SqlDriver): SoraHistoryDatabase =
    SoraHistoryDatabaseImpl(driver)

private class SoraHistoryDatabaseImpl(
  driver: SqlDriver
) : TransacterImpl(driver), SoraHistoryDatabase {
  public override val soraHistoryDatabaseQueries: SoraHistoryDatabaseQueriesImpl =
      SoraHistoryDatabaseQueriesImpl(this, driver)

  public object Schema : SqlDriver.Schema {
    public override val version: Int
      get() = 1

    public override fun create(driver: SqlDriver): Unit {
      driver.execute(null, """
          |CREATE TABLE Extrinsics (
          |    txHash TEXT NOT NULL PRIMARY KEY,
          |    signAddress TEXT NOT NULL,
          |    blockHash TEXT,
          |    module TEXT NOT NULL,
          |    method TEXT NOT NULL,
          |    networkFee TEXT NOT NULL,
          |    timestamp INTEGER NOT NULL DEFAULT 0,
          |    success INTEGER NOT NULL DEFAULT 0,
          |    batch INTEGER NOT NULL DEFAULT 0,
          |    parentHash TEXT
          |)
          """.trimMargin(), 0)
      driver.execute(null, """
          |CREATE TABLE ExtrinsicParam (
          |    extrinsicHash TEXT NOT NULL,
          |    paramName TEXT NOT NULL,
          |    paramValue TEXT NOT NULL,
          |    PRIMARY KEY (extrinsicHash, paramName),
          |    FOREIGN KEY(extrinsicHash) REFERENCES Extrinsics(txHash) ON UPDATE NO ACTION ON DELETE CASCADE
          |)
          """.trimMargin(), 0)
      driver.execute(null, """
          |CREATE TABLE SignerInfo (
          |    signAddress TEXT NOT NULL PRIMARY KEY,
          |    topTime INTEGER NOT NULL DEFAULT 0,
          |    oldTime INTEGER NOT NULL DEFAULT 0,
          |    oldCursor TEXT,
          |    endReached INTEGER NOT NULL DEFAULT 0
          |)
          """.trimMargin(), 0)
    }

    public override fun migrate(
      driver: SqlDriver,
      oldVersion: Int,
      newVersion: Int
    ): Unit {
    }
  }
}

private class SoraHistoryDatabaseQueriesImpl(
  private val database: SoraHistoryDatabaseImpl,
  private val driver: SqlDriver
) : TransacterImpl(driver), SoraHistoryDatabaseQueries {
  internal val selectExtrinsicsPaged: MutableList<Query<*>> = copyOnWriteList()

  internal val selectExtrinsicsNested: MutableList<Query<*>> = copyOnWriteList()

  internal val selectExtrinsic: MutableList<Query<*>> = copyOnWriteList()

  internal val selectExtrinsicParams: MutableList<Query<*>> = copyOnWriteList()

  internal val selectTransfersPeers: MutableList<Query<*>> = copyOnWriteList()

  internal val selectSignerInfo: MutableList<Query<*>> = copyOnWriteList()

  public override fun <T : Any> selectExtrinsicsPaged(
    signAddress: String,
    limit: Long,
    offset: Long,
    mapper: (
      txHash: String,
      signAddress: String,
      blockHash: String?,
      module: String,
      method: String,
      networkFee: String,
      timestamp: Long,
      success: Boolean,
      batch: Boolean,
      parentHash: String?
    ) -> T
  ): Query<T> = SelectExtrinsicsPagedQuery(signAddress, limit, offset) { cursor ->
    mapper(
      cursor.getString(0)!!,
      cursor.getString(1)!!,
      cursor.getString(2),
      cursor.getString(3)!!,
      cursor.getString(4)!!,
      cursor.getString(5)!!,
      cursor.getLong(6)!!,
      cursor.getLong(7)!! == 1L,
      cursor.getLong(8)!! == 1L,
      cursor.getString(9)
    )
  }

  public override fun selectExtrinsicsPaged(
    signAddress: String,
    limit: Long,
    offset: Long
  ): Query<Extrinsics> = selectExtrinsicsPaged(signAddress, limit, offset) { txHash, signAddress_,
      blockHash, module, method, networkFee, timestamp, success, batch, parentHash ->
    Extrinsics(
      txHash,
      signAddress_,
      blockHash,
      module,
      method,
      networkFee,
      timestamp,
      success,
      batch,
      parentHash
    )
  }

  public override fun <T : Any> selectExtrinsicsNested(parentHash: String?, mapper: (
    txHash: String,
    signAddress: String,
    blockHash: String?,
    module: String,
    method: String,
    networkFee: String,
    timestamp: Long,
    success: Boolean,
    batch: Boolean,
    parentHash: String?
  ) -> T): Query<T> = SelectExtrinsicsNestedQuery(parentHash) { cursor ->
    mapper(
      cursor.getString(0)!!,
      cursor.getString(1)!!,
      cursor.getString(2),
      cursor.getString(3)!!,
      cursor.getString(4)!!,
      cursor.getString(5)!!,
      cursor.getLong(6)!!,
      cursor.getLong(7)!! == 1L,
      cursor.getLong(8)!! == 1L,
      cursor.getString(9)
    )
  }

  public override fun selectExtrinsicsNested(parentHash: String?): Query<Extrinsics> =
      selectExtrinsicsNested(parentHash) { txHash, signAddress, blockHash, module, method,
      networkFee, timestamp, success, batch, parentHash_ ->
    Extrinsics(
      txHash,
      signAddress,
      blockHash,
      module,
      method,
      networkFee,
      timestamp,
      success,
      batch,
      parentHash_
    )
  }

  public override fun <T : Any> selectExtrinsic(
    hash: String,
    address: String,
    mapper: (
      txHash: String,
      signAddress: String,
      blockHash: String?,
      module: String,
      method: String,
      networkFee: String,
      timestamp: Long,
      success: Boolean,
      batch: Boolean,
      parentHash: String?
    ) -> T
  ): Query<T> = SelectExtrinsicQuery(hash, address) { cursor ->
    mapper(
      cursor.getString(0)!!,
      cursor.getString(1)!!,
      cursor.getString(2),
      cursor.getString(3)!!,
      cursor.getString(4)!!,
      cursor.getString(5)!!,
      cursor.getLong(6)!!,
      cursor.getLong(7)!! == 1L,
      cursor.getLong(8)!! == 1L,
      cursor.getString(9)
    )
  }

  public override fun selectExtrinsic(hash: String, address: String): Query<Extrinsics> =
      selectExtrinsic(hash, address) { txHash, signAddress, blockHash, module, method, networkFee,
      timestamp, success, batch, parentHash ->
    Extrinsics(
      txHash,
      signAddress,
      blockHash,
      module,
      method,
      networkFee,
      timestamp,
      success,
      batch,
      parentHash
    )
  }

  public override fun <T : Any> selectExtrinsicParams(extrinsicHash: String, mapper: (
    extrinsicHash: String,
    paramName: String,
    paramValue: String
  ) -> T): Query<T> = SelectExtrinsicParamsQuery(extrinsicHash) { cursor ->
    mapper(
      cursor.getString(0)!!,
      cursor.getString(1)!!,
      cursor.getString(2)!!
    )
  }

  public override fun selectExtrinsicParams(extrinsicHash: String): Query<ExtrinsicParam> =
      selectExtrinsicParams(extrinsicHash) { extrinsicHash_, paramName, paramValue ->
    ExtrinsicParam(
      extrinsicHash_,
      paramName,
      paramValue
    )
  }

  public override fun selectTransfersPeers(query: String): Query<String> =
      SelectTransfersPeersQuery(query) { cursor ->
    cursor.getString(0)!!
  }

  public override fun <T : Any> selectSignerInfo(signAddress: String, mapper: (
    signAddress: String,
    topTime: Long,
    oldTime: Long,
    oldCursor: String?,
    endReached: Boolean
  ) -> T): Query<T> = SelectSignerInfoQuery(signAddress) { cursor ->
    mapper(
      cursor.getString(0)!!,
      cursor.getLong(1)!!,
      cursor.getLong(2)!!,
      cursor.getString(3),
      cursor.getLong(4)!! == 1L
    )
  }

  public override fun selectSignerInfo(signAddress: String): Query<SignerInfo> =
      selectSignerInfo(signAddress) { signAddress_, topTime, oldTime, oldCursor, endReached ->
    SignerInfo(
      signAddress_,
      topTime,
      oldTime,
      oldCursor,
      endReached
    )
  }

  public override fun insertExtrinsic(
    txHash: String,
    signAddress: String,
    blockHash: String?,
    module: String,
    method: String,
    networkFee: String,
    timestamp: Long,
    success: Boolean,
    batch: Boolean,
    parentHash: String?
  ): Unit {
    driver.execute(-34905518, """
    |INSERT OR REPLACE INTO Extrinsics(txHash, signAddress, blockHash, module, method, networkFee, timestamp, success, batch, parentHash)
    |VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
    """.trimMargin(), 10) {
      bindString(1, txHash)
      bindString(2, signAddress)
      bindString(3, blockHash)
      bindString(4, module)
      bindString(5, method)
      bindString(6, networkFee)
      bindLong(7, timestamp)
      bindLong(8, if (success) 1L else 0L)
      bindLong(9, if (batch) 1L else 0L)
      bindString(10, parentHash)
    }
    notifyQueries(-34905518, {database.soraHistoryDatabaseQueries.selectExtrinsic +
        database.soraHistoryDatabaseQueries.selectExtrinsicsNested +
        database.soraHistoryDatabaseQueries.selectExtrinsicsPaged})
  }

  public override fun insertExtrinsicParam(
    extrinsicHash: String,
    paramName: String,
    paramValue: String
  ): Unit {
    driver.execute(-932943525, """
    |INSERT OR REPLACE INTO ExtrinsicParam(extrinsicHash, paramName, paramValue)
    |VALUES (?, ?, ?)
    """.trimMargin(), 3) {
      bindString(1, extrinsicHash)
      bindString(2, paramName)
      bindString(3, paramValue)
    }
    notifyQueries(-932943525, {database.soraHistoryDatabaseQueries.selectTransfersPeers +
        database.soraHistoryDatabaseQueries.selectExtrinsicParams})
  }

  public override fun insertSignerInfo(
    signAddress: String,
    topTime: Long,
    oldTime: Long,
    oldCursor: String?,
    endReached: Boolean
  ): Unit {
    driver.execute(1254906301, """
    |INSERT OR REPLACE INTO SignerInfo(signAddress, topTime, oldTime, oldCursor, endReached)
    |VALUES (?, ?, ?, ?, ?)
    """.trimMargin(), 5) {
      bindString(1, signAddress)
      bindLong(2, topTime)
      bindLong(3, oldTime)
      bindString(4, oldCursor)
      bindLong(5, if (endReached) 1L else 0L)
    }
    notifyQueries(1254906301, {database.soraHistoryDatabaseQueries.selectSignerInfo})
  }

  public override fun insertSignerInfoFull(SignerInfo: SignerInfo): Unit {
    driver.execute(-176109076, """
    |INSERT OR REPLACE INTO SignerInfo(signAddress, topTime, oldTime, oldCursor, endReached)
    |VALUES (?, ?, ?, ?, ?)
    """.trimMargin(), 5) {
      bindString(1, SignerInfo.signAddress)
      bindLong(2, SignerInfo.topTime)
      bindLong(3, SignerInfo.oldTime)
      bindString(4, SignerInfo.oldCursor)
      bindLong(5, if (SignerInfo.endReached) 1L else 0L)
    }
    notifyQueries(-176109076, {database.soraHistoryDatabaseQueries.selectSignerInfo})
  }

  public override fun removeAllExtrinsics(): Unit {
    driver.execute(532059469, """DELETE FROM Extrinsics""", 0)
    notifyQueries(532059469, {database.soraHistoryDatabaseQueries.selectTransfersPeers +
        database.soraHistoryDatabaseQueries.selectExtrinsicParams +
        database.soraHistoryDatabaseQueries.selectExtrinsic +
        database.soraHistoryDatabaseQueries.selectExtrinsicsNested +
        database.soraHistoryDatabaseQueries.selectExtrinsicsPaged})
  }

  public override fun removeExtrinsics(signAddress: String): Unit {
    driver.execute(-264856468, """DELETE FROM Extrinsics WHERE signAddress = ?""", 1) {
      bindString(1, signAddress)
    }
    notifyQueries(-264856468, {database.soraHistoryDatabaseQueries.selectTransfersPeers +
        database.soraHistoryDatabaseQueries.selectExtrinsicParams +
        database.soraHistoryDatabaseQueries.selectExtrinsic +
        database.soraHistoryDatabaseQueries.selectExtrinsicsNested +
        database.soraHistoryDatabaseQueries.selectExtrinsicsPaged})
  }

  public override fun removeAllSignerInfo(): Unit {
    driver.execute(-1425930583, """DELETE FROM SignerInfo""", 0)
    notifyQueries(-1425930583, {database.soraHistoryDatabaseQueries.selectSignerInfo})
  }

  public override fun removeSignerInfo(signAddress: String): Unit {
    driver.execute(2072120776, """DELETE FROM SignerInfo WHERE signAddress = ?""", 1) {
      bindString(1, signAddress)
    }
    notifyQueries(2072120776, {database.soraHistoryDatabaseQueries.selectSignerInfo})
  }

  private inner class SelectExtrinsicsPagedQuery<out T : Any>(
    public val signAddress: String,
    public val limit: Long,
    public val offset: Long,
    mapper: (SqlCursor) -> T
  ) : Query<T>(selectExtrinsicsPaged, mapper) {
    public override fun execute(): SqlCursor = driver.executeQuery(2123860785, """
    |SELECT Extrinsics.*
    |FROM Extrinsics WHERE signAddress = ? AND parentHash ISNULL ORDER BY timestamp DESC LIMIT ? OFFSET ?
    """.trimMargin(), 3) {
      bindString(1, signAddress)
      bindLong(2, limit)
      bindLong(3, offset)
    }

    public override fun toString(): String = "SoraHistoryDatabase.sq:selectExtrinsicsPaged"
  }

  private inner class SelectExtrinsicsNestedQuery<out T : Any>(
    public val parentHash: String?,
    mapper: (SqlCursor) -> T
  ) : Query<T>(selectExtrinsicsNested, mapper) {
    public override fun execute(): SqlCursor = driver.executeQuery(null, """
    |SELECT Extrinsics.*
    |FROM Extrinsics WHERE parentHash ${ if (parentHash == null) "IS" else "=" } ?
    """.trimMargin(), 1) {
      bindString(1, parentHash)
    }

    public override fun toString(): String = "SoraHistoryDatabase.sq:selectExtrinsicsNested"
  }

  private inner class SelectExtrinsicQuery<out T : Any>(
    public val hash: String,
    public val address: String,
    mapper: (SqlCursor) -> T
  ) : Query<T>(selectExtrinsic, mapper) {
    public override fun execute(): SqlCursor = driver.executeQuery(1354091375, """
    |SELECT Extrinsics.*
    |FROM Extrinsics WHERE txHash = ? AND signAddress = ?
    """.trimMargin(), 2) {
      bindString(1, hash)
      bindString(2, address)
    }

    public override fun toString(): String = "SoraHistoryDatabase.sq:selectExtrinsic"
  }

  private inner class SelectExtrinsicParamsQuery<out T : Any>(
    public val extrinsicHash: String,
    mapper: (SqlCursor) -> T
  ) : Query<T>(selectExtrinsicParams, mapper) {
    public override fun execute(): SqlCursor = driver.executeQuery(1138041301, """
    |SELECT ExtrinsicParam.*
    |FROM ExtrinsicParam WHERE extrinsicHash = ?
    """.trimMargin(), 1) {
      bindString(1, extrinsicHash)
    }

    public override fun toString(): String = "SoraHistoryDatabase.sq:selectExtrinsicParams"
  }

  private inner class SelectTransfersPeersQuery<out T : Any>(
    public val query: String,
    mapper: (SqlCursor) -> T
  ) : Query<T>(selectTransfersPeers, mapper) {
    public override fun execute(): SqlCursor = driver.executeQuery(-215186543, """
    |SELECT DISTINCT ExtrinsicParam.paramValue
    |FROM ExtrinsicParam WHERE (paramName = "to" OR paramName = "from") AND paramValue LIKE '%' || ? || "%"
    """.trimMargin(), 1) {
      bindString(1, query)
    }

    public override fun toString(): String = "SoraHistoryDatabase.sq:selectTransfersPeers"
  }

  private inner class SelectSignerInfoQuery<out T : Any>(
    public val signAddress: String,
    mapper: (SqlCursor) -> T
  ) : Query<T>(selectSignerInfo, mapper) {
    public override fun execute(): SqlCursor = driver.executeQuery(1364137024, """
    |SELECT SignerInfo.*
    |FROM SignerInfo WHERE signAddress = ?
    """.trimMargin(), 1) {
      bindString(1, signAddress)
    }

    public override fun toString(): String = "SoraHistoryDatabase.sq:selectSignerInfo"
  }
}
