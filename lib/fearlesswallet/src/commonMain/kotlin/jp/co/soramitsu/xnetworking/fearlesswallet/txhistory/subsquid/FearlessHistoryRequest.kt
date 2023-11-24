package jp.co.soramitsu.xnetworking.fearlesswallet.txhistory.subsquid

import jp.co.soramitsu.xnetworking.basic.common.varAfterCursor
import jp.co.soramitsu.xnetworking.basic.common.varCountRemote
import jp.co.soramitsu.xnetworking.basic.common.varMyAddress

internal fun fearlessHistorySubSquidRequest() = """
    query MyQuery($varCountRemote: Int, $varMyAddress: String, $varAfterCursor: String) {
      historyElementsConnection(where: {address_eq: $varMyAddress, OR: {dataTo_eq: $varMyAddress}}, first: $varCountRemote, after: $varAfterCursor, orderBy: timestamp_DESC) {
        pageInfo {
          endCursor
          hasNextPage
        }
        edges {
          cursor
          node {
            address
            blockHash
            blockHeight
            data
            id
            method
            module
            name
            networkFee
            timestamp
            execution {
              error {
                moduleErrorId
                moduleErrorIndex
                nonModuleErrorMessage
              }
              success
            }
          }
        }
      }
    }
""".trimIndent()
