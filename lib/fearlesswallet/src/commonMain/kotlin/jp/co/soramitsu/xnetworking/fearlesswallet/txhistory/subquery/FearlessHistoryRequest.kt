package jp.co.soramitsu.xnetworking.fearlesswallet.txhistory.subquery

import jp.co.soramitsu.xnetworking.basic.txhistory.subquery.graphqlrequest.varAfterCursor
import jp.co.soramitsu.xnetworking.basic.txhistory.subquery.graphqlrequest.varCountRemote
import jp.co.soramitsu.xnetworking.basic.txhistory.subquery.graphqlrequest.varMyAddress

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