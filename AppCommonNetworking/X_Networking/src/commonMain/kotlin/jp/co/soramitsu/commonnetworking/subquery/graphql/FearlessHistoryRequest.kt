package jp.co.soramitsu.commonnetworking.subquery.graphql

internal fun fearlessHistoryGraphQLRequest() = """
    query ($varCountRemote: Int, $varMyAddress: String, $varAfterCursor: Cursor) {
        historyElements(
            after: $varAfterCursor
            first: $varCountRemote
            orderBy: TIMESTAMP_DESC
            filter: {
                address: { equalTo: $varMyAddress }
            }
        ) {
            pageInfo {
                endCursor
                hasNextPage
            }
            nodes {
                id
                timestamp
                address
                reward
                extrinsic
                transfer
            }
        }
    }
""".trimIndent()