package jp.co.soramitsu.xnetworking.basic.common

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

const val varCountRemote = "\$countRemote"
const val varAfterCursor = "\$afterCursor"
const val varMyAddress = "\$myAddress"

@Serializable
data class BlockExplorerGraphQlRequest(
    @SerialName("query")
    val query: String,
    @SerialName("variables")
    val variables: String? = null,
)
