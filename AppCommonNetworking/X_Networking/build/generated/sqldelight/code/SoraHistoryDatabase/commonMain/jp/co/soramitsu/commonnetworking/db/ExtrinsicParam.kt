package jp.co.soramitsu.commonnetworking.db

import kotlin.String

public data class ExtrinsicParam(
  public val extrinsicHash: String,
  public val paramName: String,
  public val paramValue: String
) {
  public override fun toString(): String = """
  |ExtrinsicParam [
  |  extrinsicHash: $extrinsicHash
  |  paramName: $paramName
  |  paramValue: $paramValue
  |]
  """.trimMargin()
}
