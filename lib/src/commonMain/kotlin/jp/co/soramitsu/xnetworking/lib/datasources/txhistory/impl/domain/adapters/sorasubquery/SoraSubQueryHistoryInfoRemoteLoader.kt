package jp.co.soramitsu.xnetworking.lib.datasources.txhistory.impl.domain.adapters.sorasubquery

import jp.co.soramitsu.xnetworking.lib.datasources.chainsconfig.api.ConfigDAO
import jp.co.soramitsu.xnetworking.lib.datasources.txhistory.api.models.ChainInfo
import jp.co.soramitsu.xnetworking.lib.datasources.txhistory.api.adapters.HistoryInfoRemoteLoader
import jp.co.soramitsu.xnetworking.lib.datasources.txhistory.api.models.TxFilter
import jp.co.soramitsu.xnetworking.lib.datasources.txhistory.api.models.TxHistoryInfo
import jp.co.soramitsu.xnetworking.lib.datasources.txhistory.api.models.TxHistoryItem
import jp.co.soramitsu.xnetworking.lib.datasources.txhistory.api.models.TxHistoryItemNested
import jp.co.soramitsu.xnetworking.lib.datasources.txhistory.api.models.TxHistoryItemParam
import jp.co.soramitsu.xnetworking.lib.engines.rest.api.RestClient
import jp.co.soramitsu.xnetworking.lib.engines.utils.asJsonArrayNullable
import jp.co.soramitsu.xnetworking.lib.engines.utils.asJsonObjectNullable
import jp.co.soramitsu.xnetworking.lib.engines.utils.fieldOrNull
import jp.co.soramitsu.xnetworking.lib.engines.utils.objectOrNull
import jp.co.soramitsu.xnetworking.lib.engines.utils.primitiveOrNull
import jp.co.soramitsu.xnetworking.lib.engines.utils.wrapToGraphQLString
import kotlinx.serialization.json.JsonObject

class SoraSubQueryHistoryInfoRemoteLoader(
    private val restClient: RestClient,
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

        val response = restClient.post(
            request = SoraSubQueryRequest(
                url = configDAO.historyUrl(chainInfo.chainId),
                pageCount = pageCount,
                cursor = cursor?.wrapToGraphQLString(),
                address = signAddress.wrapToGraphQLString()
            )
        ).data.historyElements

        // Unparsing JSON scalar and normally typed contents
        val items =
            response.nodes.mapNotNull {
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
                    id = it.id ?: return@mapNotNull null,
                    blockHash = it.blockHash ?: return@mapNotNull null,
                    module = it.module ?: return@mapNotNull null,
                    method = it.method ?: return@mapNotNull null,
                    timestamp = it.timestamp.toString(),
                    networkFee = it.networkFee ?: return@mapNotNull null,
                    success = wasOperationSuccessful,
                    data = txHistoryItemParams,
                    nestedData = nestedData,
                )
            }

        return TxHistoryInfo(
            endCursor = response.pageInfo.endCursor,
            endReached = !(response.pageInfo.hasNextPage ?: false),
            items = items
        )
    }

}