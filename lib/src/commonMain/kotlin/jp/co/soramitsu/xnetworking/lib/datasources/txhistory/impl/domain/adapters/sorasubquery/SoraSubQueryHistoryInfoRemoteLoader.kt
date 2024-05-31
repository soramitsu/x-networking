package jp.co.soramitsu.xnetworking.lib.datasources.txhistory.impl.domain.adapters.sorasubquery

import com.apollographql.apollo3.api.Optional
import jp.co.soramitsu.xnetworking.lib.datasources.chainsconfig.api.ConfigDAO
import jp.co.soramitsu.xnetworking.lib.datasources.txhistory.api.models.ChainInfo
import jp.co.soramitsu.xnetworking.lib.datasources.txhistory.api.adapters.HistoryInfoRemoteLoader
import jp.co.soramitsu.xnetworking.lib.datasources.txhistory.api.models.TxFilter
import jp.co.soramitsu.xnetworking.lib.datasources.txhistory.api.models.TxHistoryInfo
import jp.co.soramitsu.xnetworking.lib.datasources.txhistory.api.models.TxHistoryItem
import jp.co.soramitsu.xnetworking.lib.datasources.txhistory.api.models.TxHistoryItemNested
import jp.co.soramitsu.xnetworking.lib.datasources.txhistory.api.models.TxHistoryItemParam
import jp.co.soramitsu.xnetworking.lib.engines.apollo.api.ApolloClientStore
import jp.co.soramitsu.xnetworking.lib.engines.utils.asJsonArrayNullable
import jp.co.soramitsu.xnetworking.lib.engines.utils.asJsonObjectNullable
import jp.co.soramitsu.xnetworking.lib.engines.utils.fieldOrNull
import jp.co.soramitsu.xnetworking.lib.engines.utils.objectOrNull
import jp.co.soramitsu.xnetworking.lib.engines.utils.primitiveOrNull
import jp.co.soramitsu.xnetworking.sorawallet.GetSoraHistoryElementsQuery
import jp.co.soramitsu.xnetworking.sorawallet.type.HistoryElementFilter
import jp.co.soramitsu.xnetworking.sorawallet.type.HistoryElementsOrderBy
import jp.co.soramitsu.xnetworking.sorawallet.type.JSONFilter
import jp.co.soramitsu.xnetworking.sorawallet.type.StringFilter
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive

class SoraSubQueryHistoryInfoRemoteLoader(
    private val apolloClientStore: ApolloClientStore,
    private val configDAO: ConfigDAO
): HistoryInfoRemoteLoader() {

    override suspend fun loadHistoryInfo(
        pageCount: Int,
        cursor: String?,
        signAddress: String,
        chainInfo: ChainInfo,
        filters: Set<TxFilter>
    ): TxHistoryInfo {
        if (filters.isEmpty()) {
            return TxHistoryInfo(
                endCursor = cursor,
                endReached = false,
                items = emptyList()
            )
        }

        val response = checkNotNull(
            apolloClientStore.query(
                serverUrl = configDAO.historyUrl(chainInfo.chainId),
                GetSoraHistoryElementsQuery(
                    pageCount = Optional.present(pageCount),
                    cursor = Optional.present(cursor),
                    orderBy = Optional.present(listOf(HistoryElementsOrderBy.TIMESTAMP_DESC)),
                    filter = Optional.present(createHistoryElementsFilter(signAddress))
                )
            ).historyElements
        ) { "GetHistoryElementsQuery response is null" }

        // Unparsing JSON scalar and normally typed contents
        val items =
            response.nodes.filterNotNull().map {
                val wasOperationSuccessful = it.execution.fieldOrNull("success").toBoolean()

                val txHistoryItemParams = it.data.asJsonObjectNullable?.map { mapItem ->
                    TxHistoryItemParam(
                        paramName = mapItem.key,
                        paramValue = mapItem.value.primitiveOrNull().orEmpty()
                    )
                }

                val nestedData = it.data?.asJsonArrayNullable
                    ?.filterIsInstance<JsonObject>()
                    ?.map { json ->
                        TxHistoryItemNested(
                            hash = json.fieldOrNull("hash").orEmpty(),
                            module = json.fieldOrNull("module").orEmpty(),
                            method = json.fieldOrNull("method").orEmpty(),
                            data = json.objectOrNull("data")
                                .objectOrNull("args")?.map { mapItem ->
                                    TxHistoryItemParam(
                                        paramName = mapItem.key,
                                        paramValue = mapItem.value.primitiveOrNull().orEmpty()
                                    )
                                } ?: emptyList()
                        )
                    }

                TxHistoryItem(
                    id = it.id,
                    blockHash = it.blockHash,
                    module = it.module,
                    method = it.method,
                    timestamp = it.timestamp.toString(),
                    networkFee = it.networkFee,
                    success = wasOperationSuccessful,
                    data = txHistoryItemParams,
                    nestedData = nestedData,
                )
            }

        return TxHistoryInfo(
            endCursor = response.pageInfo.endCursor,
            endReached = !response.pageInfo.hasNextPage,
            items = items
        )
    }

    internal companion object {
        fun createHistoryElementsFilter(signAddress: String) =
            HistoryElementFilter(
                or = Optional.present(
                    value = listOf(
                        HistoryElementFilter(
                            address = Optional.present(
                                value = StringFilter(
                                    equalTo = Optional.present(value = signAddress)
                                )
                            ),
                            or = Optional.present(
                                value = listOf(
                                    HistoryElementFilter(
                                        module = Optional.present(
                                            value = StringFilter(
                                                equalTo = Optional.present(value = "assets")
                                            )
                                        ),
                                        method = Optional.present(
                                            value = StringFilter(
                                                equalTo = Optional.present(value = "transfer")
                                            )
                                        )
                                    ),
                                    HistoryElementFilter(
                                        module = Optional.present(
                                            value = StringFilter(
                                                equalTo = Optional.present(value = "liquidityProxy")
                                            )
                                        ),
                                        method = Optional.present(
                                            value = StringFilter(
                                                equalTo = Optional.present(value = "swap")
                                            )
                                        )
                                    ),
                                    HistoryElementFilter(
                                        module = Optional.present(
                                            value = StringFilter(
                                                equalTo = Optional.present(value = "poolXYK")
                                            )
                                        ),
                                        method = Optional.present(
                                            value = StringFilter(
                                                equalTo = Optional.present(value = "depositLiquidity")
                                            )
                                        )
                                    ),
                                    HistoryElementFilter(
                                        data = Optional.present(
                                            value = JSONFilter(
                                                contains = Optional.present(
                                                    value = JsonObject(
                                                        content = mapOf("method" to JsonPrimitive("depositLiquidity"))
                                                    )
                                                )
                                            )
                                        )
                                    ),
                                    HistoryElementFilter(
                                        module = Optional.present(
                                            value = StringFilter(
                                                equalTo = Optional.present(value = "poolXYK")
                                            )
                                        ),
                                        method = Optional.present(
                                            value = StringFilter(
                                                equalTo = Optional.present(value = "withdrawLiquidity")
                                            )
                                        )
                                    ),
                                    HistoryElementFilter(
                                        data = Optional.present(
                                            value = JSONFilter(
                                                contains = Optional.present(
                                                    value = JsonObject(
                                                        content = mapOf("method" to JsonPrimitive("withdrawLiquidity"))
                                                    )
                                                )
                                            )
                                        )
                                    ),
                                    HistoryElementFilter(
                                        module = Optional.present(
                                            value = StringFilter(
                                                equalTo = Optional.present(value = "referrals")
                                            )
                                        ),
                                    ),
                                    HistoryElementFilter(
                                        module = Optional.present(
                                            value = StringFilter(
                                                equalTo = Optional.present(value = "ethBridge")
                                            )
                                        ),
                                        method = Optional.present(
                                            value = StringFilter(
                                                equalTo = Optional.present(value = "transferToSidechain")
                                            )
                                        )
                                    ),
                                )
                            )
                        ),
                        HistoryElementFilter(
                            data = Optional.present(
                                value = JSONFilter(
                                    contains = Optional.present(
                                        value = JsonObject(
                                            content = mapOf("to" to JsonPrimitive(signAddress))
                                        )
                                    )
                                )
                            ),
                            module = Optional.present(
                                value = StringFilter(
                                    equalTo = Optional.present(value = "assets")
                                )
                            ),
                            method = Optional.present(
                                value = StringFilter(
                                    equalTo = Optional.present(value = "transfer")
                                )
                            ),
                            execution = Optional.present(
                                value = JSONFilter(
                                    contains = Optional.present(
                                        value = JsonObject(
                                            content = mapOf("success" to JsonPrimitive(true))
                                        )
                                    )
                                )
                            )
                        ),
                        HistoryElementFilter(
                            data = Optional.present(
                                value = JSONFilter(
                                    contains = Optional.present(
                                        value = JsonObject(
                                            content = mapOf("to" to JsonPrimitive(signAddress))
                                        )
                                    )
                                )
                            ),
                            module = Optional.present(
                                value = StringFilter(
                                    equalTo = Optional.present(value = "referrals")
                                )
                            ),
                            method = Optional.present(
                                value = StringFilter(
                                    equalTo = Optional.present(value = "setReferrer")
                                )
                            ),
                            execution = Optional.present(
                                value = JSONFilter(
                                    contains = Optional.present(
                                        value = JsonObject(
                                            content = mapOf("success" to JsonPrimitive(true))
                                        )
                                    )
                                )
                            )
                        )
                    )
                )
            )
    }

}