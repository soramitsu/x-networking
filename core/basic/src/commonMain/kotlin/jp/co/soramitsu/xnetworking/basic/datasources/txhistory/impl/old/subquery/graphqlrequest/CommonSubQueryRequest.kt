package jp.co.soramitsu.xnetworking.basic.datasources.txhistory.impl.old.subquery.graphqlrequest

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SubQueryRequest(
    @SerialName("query")
    val query: String,
    @SerialName("variables")
    val variables: String? = null,
)

const val varCountRemote = "\$countRemote"
const val varAfterCursor = "\$afterCursor"
const val varMyAddress = "\$myAddress"

internal fun txHistoryGraphQLVariables(
    countRemote: Int,
    myAddress: String,
    cursor: String,
) = """
    {"countRemote": $countRemote, 
    "myAddress": "$myAddress",
    "afterCursor": "$cursor"}
    """.trimIndent()
