package jp.co.soramitsu.commonnetworking.db

import kotlin.Boolean
import kotlin.Long
import kotlin.String

public data class Extrinsics(
  public val txHash: String,
  public val signAddress: String,
  public val blockHash: String?,
  public val module: String,
  public val method: String,
  public val networkFee: String,
  public val timestamp: Long,
  public val success: Boolean,
  public val batch: Boolean,
  public val parentHash: String?
) {
  public override fun toString(): String = """
  |Extrinsics [
  |  txHash: $txHash
  |  signAddress: $signAddress
  |  blockHash: $blockHash
  |  module: $module
  |  method: $method
  |  networkFee: $networkFee
  |  timestamp: $timestamp
  |  success: $success
  |  batch: $batch
  |  parentHash: $parentHash
  |]
  """.trimMargin()
}
