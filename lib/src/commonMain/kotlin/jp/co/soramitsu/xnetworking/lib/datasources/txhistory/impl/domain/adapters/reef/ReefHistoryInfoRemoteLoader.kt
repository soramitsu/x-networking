package jp.co.soramitsu.xnetworking.lib.datasources.txhistory.impl.domain.adapters.reef

import jp.co.soramitsu.xnetworking.lib.datasources.chainsconfig.api.ConfigDAO
import jp.co.soramitsu.xnetworking.lib.datasources.txhistory.api.models.ChainInfo
import jp.co.soramitsu.xnetworking.lib.datasources.txhistory.api.adapters.HistoryInfoRemoteLoader
import jp.co.soramitsu.xnetworking.lib.datasources.txhistory.api.utils.PackedCursor
import jp.co.soramitsu.xnetworking.lib.datasources.txhistory.api.models.TxFilter
import jp.co.soramitsu.xnetworking.lib.datasources.txhistory.api.models.TxHistoryInfo
import jp.co.soramitsu.xnetworking.lib.datasources.txhistory.api.models.TxHistoryItem
import jp.co.soramitsu.xnetworking.lib.datasources.txhistory.api.models.TxHistoryItemParam
import jp.co.soramitsu.xnetworking.lib.datasources.txhistory.impl.utils.create
import jp.co.soramitsu.xnetworking.lib.engines.rest.api.RestClient

class ReefHistoryInfoRemoteLoader(
    private val configDAO: ConfigDAO,
    private val restClient: RestClient
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

        val items = mutableListOf<TxHistoryItem>()

        val packedCursor by PackedCursor.create(cursor)
        var isNextCursorAvailable = false

        if (TxFilter.TRANSFER in filters) {
            val response = restClient.post(
                request = ReefRequest(
                    url = configDAO.historyUrl(chainInfo.chainId),
                    address = signAddress,
                    limit = pageCount,
                    cursor = packedCursor["transfers"],
                    txFilter = TxFilter.TRANSFER
                )
            ).data.transfersConnection

            response?.edges?.map {
                val transferId = requireNotNull(
                    it.node.id
                ) { "Can not create txHistoryItem, id must not be null" }

                TxHistoryItem(
                    id = transferId,
                    blockHash = it.node.extrinsicHash ?: "",
                    module = "transfer",
                    method = "",
                    timestamp = it.node.timestamp,
                    nestedData = emptyList(),
                    networkFee = it.node.signedData?.fee?.partialFee ?: "",
                    success = it.node.success,
                    data = listOf(
                        TxHistoryItemParam(
                            "amount",
                            it.node.amount
                        ),
                        TxHistoryItemParam(
                            "to",
                            it.node.to.id
                        ),
                        TxHistoryItemParam(
                            "from",
                            it.node.from.id
                        ),
                    )
                )
            }?.run {
                items += this

                isNextCursorAvailable =
                    isNextCursorAvailable || response.pageInfo.hasNextPage

                packedCursor["transfers"] = response.pageInfo.endCursor
            }
        }

        if (TxFilter.REWARD in filters) {
            val response = restClient.post(
                request = ReefRequest(
                    url = configDAO.historyUrl(chainInfo.chainId),
                    address = signAddress,
                    limit = pageCount,
                    cursor = packedCursor["rewards"],
                    txFilter = TxFilter.REWARD
                )
            ).data.rewardsConnection

            response?.edges?.map {
                TxHistoryItem(
                    id = it.node.id,
                    blockHash = "",
                    module = "reward",
                    method = "",
                    timestamp = it.node.timestamp,
                    nestedData = emptyList(),
                    networkFee = "0",
                    success = true,
                    data = listOf(
                        TxHistoryItemParam(
                            "accountId",
                            it.node.signer.id
                        ),
                        TxHistoryItemParam(
                            "amount",
                            it.node.amount
                        )
                    )
                )
            }?.run {
                items += this

                isNextCursorAvailable =
                    isNextCursorAvailable || response.pageInfo.hasNextPage

                packedCursor["rewards"] = response.pageInfo.endCursor
            }
        }

        if (TxFilter.EXTRINSIC in filters) {
            val response = restClient.post(
                request = ReefRequest(
                    url = configDAO.historyUrl(chainInfo.chainId),
                    address = signAddress,
                    limit = pageCount,
                    cursor = packedCursor["extrinsics"],
                    txFilter = TxFilter.EXTRINSIC
                )
            ).data.extrinsicsConnection

            response?.edges?.map {
                TxHistoryItem(
                    id = it.node.id,
                    blockHash = it.node.hash,
                    module = "extrinsic",
                    method = "",
                    timestamp = it.node.timestamp,
                    nestedData = emptyList(),
                    networkFee = it.node.signedData?.fee?.partialFee ?: "",
                    success = true,
                    data = listOf(
                        TxHistoryItemParam(
                            "accountId",
                            it.node.signer
                        ),
                        TxHistoryItemParam(
                            "section",
                            it.node.section
                        ),
                        TxHistoryItemParam(
                            "method",
                            it.node.method
                        ),
                    )
                )
            }?.run {
                items += this

                isNextCursorAvailable =
                    isNextCursorAvailable || response.pageInfo.hasNextPage

                packedCursor["extrinsics"] = response.pageInfo.endCursor
            }
        }

        return TxHistoryInfo(
            endCursor = packedCursor.pack(),
            endReached = !isNextCursorAvailable,
            items = items
        )
    }

}