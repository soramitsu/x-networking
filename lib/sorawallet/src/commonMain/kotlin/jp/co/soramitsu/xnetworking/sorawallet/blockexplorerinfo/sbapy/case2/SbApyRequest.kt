package jp.co.soramitsu.xnetworking.sorawallet.blockexplorerinfo.sbapy.case2

internal fun graphQLRequestSoraWalletSbApyCase2(
    cursor: String,
) = """ 
    query
                FiatPriceQuery {
                    entities: poolXYKs(
                      first: 100
                      after: "$cursor" ) {
                          nodes {
                            id strategicBonusApy
                          }
                          pageInfo {
                            hasNextPage endCursor
                          }
                    }
                }
""".trimIndent()
