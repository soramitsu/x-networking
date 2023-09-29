package jp.co.soramitsu.xnetworking.sorawallet.core.datasources.blockexplorer.impl.rest.fiat.case2

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