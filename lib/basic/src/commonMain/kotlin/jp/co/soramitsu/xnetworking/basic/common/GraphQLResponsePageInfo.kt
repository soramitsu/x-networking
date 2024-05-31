package jp.co.soramitsu.xnetworking.basic.common

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ResponsePageInfo(
    @SerialName("hasNextPage")
    val hasNextPage: Boolean,
    @SerialName("endCursor")
    val endCursor: String?,
)
