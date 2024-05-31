package jp.co.soramitsu.xnetworking.sorawallet.blockexplorerinfo.assets.case1

internal fun graphQLRequestSoraWalletAssetsCase1(
    cursor: String,
    tokenIds: String,
) = """
    query AssetsQuery {
          data: assets(
            after: "$cursor"
            filter: {and: [{id:{in:[$tokenIds]}}]}
            ) {
              edges {
                node {
                  id
                  priceChangeDay
                  liquidity
                }
              }         
              pageInfo {
                hasNextPage
                endCursor
              }
            }
    }
""".trimIndent()
