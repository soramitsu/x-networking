package jp.co.soramitsu.xnetworking.basic.txhistory.subquery.graphqlrequest

internal fun txHistorySubQueryVariables(
    countRemote: Int,
    myAddress: String,
    cursor: String,
) = """
    {
      "myAddress": "$myAddress",
      "countRemote": $countRemote,
      "afterCursor": "$cursor"
    }
    """.trimIndent()
