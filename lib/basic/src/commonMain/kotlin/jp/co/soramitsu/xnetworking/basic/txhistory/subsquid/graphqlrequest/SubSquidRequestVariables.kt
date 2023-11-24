package jp.co.soramitsu.xnetworking.basic.txhistory.subsquid.graphqlrequest

internal fun txHistorySubSquidVariables(
    countRemote: Int,
    cursor: String? = null,
    myAddress: String,
) = """
    {
      "myAddress": "$myAddress",
      "countRemote": $countRemote,
      "afterCursor": "$cursor"
    }
    """.trimIndent()
