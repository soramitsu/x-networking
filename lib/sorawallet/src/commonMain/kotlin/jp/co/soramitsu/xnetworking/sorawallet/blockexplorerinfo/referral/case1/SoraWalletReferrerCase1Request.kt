package jp.co.soramitsu.xnetworking.sorawallet.blockexplorerinfo.referral.case1

internal fun graphQLRequestSoraWalletReferrerCase1(
    address: String,
    cursor: String,
) = """
    query {
        referrerRewards(
          first: 100
          after: "$cursor"
          filter: { referrer: {equalTo: "$address"} }
        ) {
            pageInfo {
                hasNextPage
                endCursor
            }
            nodes {
                referral
                amount
            }
        }
    }
""".trimIndent()
