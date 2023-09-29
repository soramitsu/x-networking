package jp.co.soramitsu.xnetworking.sorawallet.core.datasources.blockexplorer.impl.rest.assets.case0

internal fun graphQLRequestSoraWalletAssetsCase0(
    cursor: String,
    tokenIds: String,
    timestamp: String,
) = """
    query AssetsQuery {
          entities: assets(
            first: 100 
            after: "$cursor"
            filter: {and: [{id:{in:[$tokenIds]}}]}
            ) {
              nodes {
                id
                liquidity
                hourSnapshots: data(
                        filter: {
                            and: [
                                {
                                    timestamp: {
                                        greaterThanOrEqualTo: $timestamp
                                    }
                                }, {
                                    type: {
                                        equalTo: HOUR
                                    }
                                }
                            ]
                        } 
                        orderBy: [TIMESTAMP_DESC]
                    ) {
                        nodes {
                            priceUSD
                        }
                    }
              }         
              pageInfo {
                hasNextPage
                endCursor
              }
            }
    }
""".trimIndent()
