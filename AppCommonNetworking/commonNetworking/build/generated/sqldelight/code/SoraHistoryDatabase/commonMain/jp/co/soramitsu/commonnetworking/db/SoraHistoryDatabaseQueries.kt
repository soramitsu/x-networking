package jp.co.soramitsu.commonnetworking.db

import com.squareup.sqldelight.Query
import com.squareup.sqldelight.Transacter
import kotlin.Any
import kotlin.Boolean
import kotlin.Long
import kotlin.String
import kotlin.Unit

public interface SoraHistoryDatabaseQueries : Transacter {
  public fun <T : Any> selectExtrinsicsPaged(
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
  ): Query<T>

  public fun selectExtrinsicsPaged(
    signAddress: String,
    limit: Long,
    offset: Long
  ): Query<Extrinsics>

  public fun <T : Any> selectExtrinsicsNested(parentHash: String?, mapper: (
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
  ) -> T): Query<T>

  public fun selectExtrinsicsNested(parentHash: String?): Query<Extrinsics>

  public fun <T : Any> selectExtrinsic(
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
  ): Query<T>

  public fun selectExtrinsic(hash: String, address: String): Query<Extrinsics>

  public fun <T : Any> selectExtrinsicParams(extrinsicHash: String, mapper: (
    extrinsicHash: String,
    paramName: String,
    paramValue: String
  ) -> T): Query<T>

  public fun selectExtrinsicParams(extrinsicHash: String): Query<ExtrinsicParam>

  public fun selectTransfersPeers(query: String): Query<String>

  public fun <T : Any> selectSignerInfo(signAddress: String, mapper: (
    signAddress: String,
    topTime: Long,
    oldTime: Long,
    oldCursor: String?,
    endReached: Boolean
  ) -> T): Query<T>

  public fun selectSignerInfo(signAddress: String): Query<SignerInfo>

  public fun insertExtrinsic(
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
  ): Unit

  public fun insertExtrinsicParam(
    extrinsicHash: String,
    paramName: String,
    paramValue: String
  ): Unit

  public fun insertSignerInfo(
    signAddress: String,
    topTime: Long,
    oldTime: Long,
    oldCursor: String?,
    endReached: Boolean
  ): Unit

  public fun insertSignerInfoFull(SignerInfo: SignerInfo): Unit

  public fun removeAllExtrinsics(): Unit

  public fun removeExtrinsics(signAddress: String): Unit

  public fun removeAllSignerInfo(): Unit

  public fun removeSignerInfo(signAddress: String): Unit
}
