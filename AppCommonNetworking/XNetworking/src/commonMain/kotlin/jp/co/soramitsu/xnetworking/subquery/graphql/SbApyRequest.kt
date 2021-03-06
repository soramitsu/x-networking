package jp.co.soramitsu.xnetworking.subquery.graphql

internal fun sbApyGraphQLRequest() = """ query {
                poolXYKEntities (
                    first: 1
                    orderBy: UPDATED_DESC
                  )
                  {
                    nodes {
                      pools {
                        edges {
                          node {
                            targetAssetId,
                            priceUSD,
                            strategicBonusApy
                          }
                        }
                      }
                    }
                  }
                }
                    """.trimIndent()
