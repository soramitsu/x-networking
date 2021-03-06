package jp.co.soramitsu.xnetworking.subquery.graphql

internal fun soraHistoryGraphQLRequest(
) = """
                    query ($varCountRemote: Int, $varMyAddress: String, $varAfterCursor: Cursor) { 
                      historyElements( 
                        first: $varCountRemote 
                        orderBy: TIMESTAMP_DESC 
                        after: $varAfterCursor 
                        filter: { 
                          or: [ 
                            { 
                              address: { equalTo: $varMyAddress } 
                              or: [
                                { module: { equalTo: "assets" } method: { equalTo: "transfer" }} 
                                { module: { equalTo: "liquidityProxy" } method: { equalTo: "swap" }} 
                                { module: { equalTo: "poolXYK" } method: { equalTo: "depositLiquidity" }} 
                                { data: { contains: [{ method: "depositLiquidity" }] }} 
                                { module: { equalTo: "poolXYK" } method: { equalTo: "withdrawLiquidity" }} 
                                { data: { contains: [{ method: "withdrawLiquidity" }] }} 
                                { module: { equalTo: "referrals" } }
                              ] 
                            } 
                            { 
                              data: { contains: { to: $varMyAddress } }
                              module: { equalTo: "assets" } method: { equalTo: "transfer" }
                              execution: { contains: { success: true } }
                            } 
                            {
                              data: { contains: { to: $varMyAddress } }
                              module: { equalTo: "referrals" } method: { equalTo: "setReferrer" }
                              execution: { contains: { success: true } }
                            }
                          ] 
                        } 
                      ) { 
                        nodes { 
                          id
                          blockHash
                          module
                          method
                          address
                          networkFee
                          execution
                          timestamp
                          data 
                        }
                        pageInfo {
                          endCursor
                          hasNextPage
                        }
                      } 
                    }
                """.trimIndent()
