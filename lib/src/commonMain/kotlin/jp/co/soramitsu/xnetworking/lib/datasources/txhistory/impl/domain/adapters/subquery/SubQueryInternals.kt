package jp.co.soramitsu.xnetworking.lib.datasources.txhistory.impl.domain.adapters.subquery

import jp.co.soramitsu.xnetworking.lib.datasources.txhistory.api.models.TxFilter
import jp.co.soramitsu.xnetworking.lib.engines.utils.GraphQLResponseDataWrapper
import jp.co.soramitsu.xnetworking.lib.engines.utils.GraphQLSerializableRequestWrapper
import jp.co.soramitsu.xnetworking.lib.engines.utils.JsonPostRequest
import jp.co.soramitsu.xnetworking.lib.datasources.txhistory.impl.utils.GraphQLExts.and
import jp.co.soramitsu.xnetworking.lib.datasources.txhistory.impl.utils.GraphQLExts.anyOf
import jp.co.soramitsu.xnetworking.lib.datasources.txhistory.impl.utils.GraphQLExts.not
import jp.co.soramitsu.xnetworking.lib.datasources.txhistory.impl.utils.GraphQLExts.or
import jp.co.soramitsu.xnetworking.lib.engines.rest.api.RestClient
import kotlinx.serialization.Serializable

@Suppress("FunctionName")
internal fun SubQueryRequest(
    url: String,
    cursor: String?,
    pageCount: Int,
    address: String,
    filters: Set<TxFilter>,
    requestRewards: Boolean
) = JsonPostRequest(
    url = url,
    body = GraphQLSerializableRequestWrapper(
        """
            query {
                historyElements(
                    after: ${if (cursor == null) null else "\"$cursor\""},
                    first: $pageCount,
                    orderBy: TIMESTAMP_DESC,
                    filter: { 
                        address:{ equalTo: "$address"},
                        ${if (filters.isNotEmpty()) filters.toQueryFilter() else ""}
                    }
                ) {
                    pageInfo {
                        startCursor,
                        endCursor
                    },
                    nodes {
                        id
                        timestamp
                        address
                        ${if (requestRewards) "reward" else ""}
                        extrinsic
                        transfer
                    }
                }
            }
        """.trimIndent()
    ),
    responseDeserializer = GraphQLResponseDataWrapper.serializer(
        SubQueryResponse.serializer()
    )
)


/*
    or: [ {transfer: { notEqualTo: "null"} },  {extrinsic: { notEqualTo: "null"} } ]
 */
private fun Set<TxFilter>.toQueryFilter(): String {
    // optimize query in case all filters are on
    if (allFiltersIncluded()) {
        return ""
    }

    val typeExpressions = map {
        if (it == TxFilter.EXTRINSIC) {
            createExtrinsicExpression()
        } else {
            hasType(it.filterName)
        }
    }

    val result = anyOf(typeExpressions)

    return result
}

private fun createExtrinsicExpression(): String {
    val exists = hasType(TxFilter.EXTRINSIC.filterName)

    val restrictedModulesList = EXTRINSIC_RESTRICTIONS.map {
        val restrictedCallsExpressions = it.restrictedCalls.map(::callNamed)

        and(
            moduleNamed(it.moduleName),
            anyOf(restrictedCallsExpressions)
        )
    }

    val hasRestrictedModules = or(restrictedModulesList)

    return and(
        exists,
        not(hasRestrictedModules)
    )
}

private fun callNamed(callName: String) = "extrinsic: {contains: {call: \"$callName\"}}"
private fun moduleNamed(moduleName: String) = "extrinsic: {contains: {module: \"$moduleName\"}}"
private fun hasType(typeName: String) = "$typeName: {isNull: false}"

private val TxFilter.filterName
    get() = name.lowercase()

private fun Set<TxFilter>.allFiltersIncluded(): Boolean {
    return size == TxFilter.values().size
}

val requestContentType: RestClient.ContentType = RestClient.ContentType.JSON

private val EXTRINSIC_RESTRICTIONS = listOf(
    ModuleRestriction(
        moduleName = "balances",
        restrictedCalls = listOf(
            "transfer",
            "transferKeepAlive",
            "forceTransfer"
        )
    )
)

private class ModuleRestriction(
    val moduleName: String,
    val restrictedCalls: List<String>
)

@Serializable
internal class SubQueryResponse(
    val historyElements: HistoryElements
) {
    @Serializable
    class HistoryElements(
        val pageInfo: PageInfo,
        val nodes: Array<Node>
    ) {
        @Serializable
        class PageInfo(
            val startCursor: String?,
            val endCursor: String?
        )

        @Serializable
        class Node(
            val id: String,
            val timestamp: String,
            val address: String,
            val reward: Rewards?,
            val transfer: Transfer?,
            val extrinsic: Extrinsic?
        ) {
            @Serializable
            class Rewards(
                val era: Int,
                val amount: String,
                val isReward: Boolean,
                val validator: String,
                val assetId: String?
            )

            @Serializable
            class Transfer(
                val amount: String,
                val to: String,
                val from: String,
                val fee: String,
                val block: String,
                val success: Boolean,
                val extrinsicHash: String?, // nullable since not all transfers not hash hash on SubQuery
                val assetId: String?
            )

            @Serializable
            class Extrinsic(
                val hash: String,
                val module: String,
                val call: String,
                val fee: String,
                val success: Boolean,
                val assetId: String?
            )
        }
    }
}