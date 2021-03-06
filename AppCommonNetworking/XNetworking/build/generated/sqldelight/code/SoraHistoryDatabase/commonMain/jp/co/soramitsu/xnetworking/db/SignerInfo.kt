package jp.co.soramitsu.xnetworking.db

import kotlin.Boolean
import kotlin.Long
import kotlin.String

public data class SignerInfo(
  public val signAddress: String,
  public val topTime: Long,
  public val oldTime: Long,
  public val oldCursor: String?,
  public val endReached: Boolean,
  public val networkName: String
) {
  public override fun toString(): String = """
  |SignerInfo [
  |  signAddress: $signAddress
  |  topTime: $topTime
  |  oldTime: $oldTime
  |  oldCursor: $oldCursor
  |  endReached: $endReached
  |  networkName: $networkName
  |]
  """.trimMargin()
}
