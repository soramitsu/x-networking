package jp.co.soramitsu.xnetworking.fearlesswallet.core.datasources.txhistory.subquery

import jp.co.soramitsu.xnetworking.basic.datasources.txhistory.impl.old.subquery.graphqlrequest.varAfterCursor
import jp.co.soramitsu.xnetworking.basic.datasources.txhistory.impl.old.subquery.graphqlrequest.varCountRemote
import jp.co.soramitsu.xnetworking.basic.datasources.txhistory.impl.old.subquery.graphqlrequest.varMyAddress
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