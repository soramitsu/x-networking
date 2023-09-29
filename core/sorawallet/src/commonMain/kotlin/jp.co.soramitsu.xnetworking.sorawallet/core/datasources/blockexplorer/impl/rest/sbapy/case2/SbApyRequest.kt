package jp.co.soramitsu.xnetworking.sorawallet.core.datasources.blockexplorer.impl.rest.sbapy.case2

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
