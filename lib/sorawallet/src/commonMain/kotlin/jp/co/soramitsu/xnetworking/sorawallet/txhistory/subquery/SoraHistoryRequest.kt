package jp.co.soramitsu.xnetworking.sorawallet.txhistory.subquery

import jp.co.soramitsu.xnetworking.basic.txhistory.subquery.graphqlrequest.varAfterCursor
import jp.co.soramitsu.xnetworking.basic.txhistory.subquery.graphqlrequest.varCountRemote
import jp.co.soramitsu.xnetworking.basic.txhistory.subquery.graphqlrequest.varMyAddress

internal fun soraHistoryGraphQLRequest(
) = """
                    query ($varCountRemote: Int, $varMyAddress: String, $varAfterCursor: Cursor) { 
                      historyElements( 
                        first: $varCountRemote 
                        orderBy: TIMESTAMP_DESC 
                        after: $varAfterCursor 
                        filter: {
                          and: [
                            {
                              or: [ 
                                {
                                  or: [
                                    { module: { equalTo: "liquidityProxy" } method: { equalTo: "swapTransfer" }} 
                                    { module: { equalTo: "liquidityProxy" } method: { equalTo: "swapTransferBatch" }}
                                  ]
                                }
                                { 
                                  or: [
                                    { module: { equalTo: "assets" } method: { equalToInsensitive: "transfer" }} 
                                    { module: { equalTo: "liquidityProxy" } method: { equalTo: "xorlessTransfer" }} 
                                    { module: { equalTo: "liquidityProxy" } method: { equalTo: "swap" }} 
                                    { module: { equalTo: "poolXYK" } method: { equalTo: "depositLiquidity" }} 
                                    { data: { contains: [{ method: "depositLiquidity" }] }} 
                                    { module: { equalTo: "poolXYK" } method: { equalTo: "withdrawLiquidity" }} 
                                    { data: { contains: [{ method: "withdrawLiquidity" }] }} 
                                    { module: { equalTo: "referrals" } }
                                    { module: { equalTo: "ethBridge" } method: { equalTo: "transferToSidechain" }} 
                                    { module: { equalTo: "demeterFarmingPlatform" } method: { equalTo: "deposit" }} 
                                    { module: { equalTo: "demeterFarmingPlatform" } method: { equalTo: "withdraw" }} 
                                    { module: { equalTo: "demeterFarmingPlatform" } method: { equalTo: "getRewards" }} 
                                    { module: { equalTo: "orderBook" } method: { equalTo: "placeLimitOrder" }} 
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
                            {
                              or: [
                                { address: { equalTo: $varMyAddress } }
                                { dataTo: { equalTo: $varMyAddress } }
                              ]
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
                          dataTo
                        }
                        pageInfo {
                          endCursor
                          hasNextPage
                        }
                      } 
                    }
                """.trimIndent()
