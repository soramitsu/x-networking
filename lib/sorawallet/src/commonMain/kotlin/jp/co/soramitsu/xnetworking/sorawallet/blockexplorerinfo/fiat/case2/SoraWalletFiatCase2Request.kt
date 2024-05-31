package jp.co.soramitsu.xnetworking.sorawallet.blockexplorerinfo.fiat.case2

internal fun graphQLRequestSoraWalletFiatCase2(
    cursor: String,
) = """
    query FiatPriceQuery {
          entities: assets(
            first: 100 
            after: "$cursor") {
              nodes {
                id
                priceUSD
              }         
              pageInfo {
                hasNextPage
                endCursor
              }
            }
    }
""".trimIndent()