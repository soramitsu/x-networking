package jp.co.soramitsu.xnetworking.lib.datasources.txhistory.impl.domain.adapters.giantsquid

import jp.co.soramitsu.xnetworking.lib.datasources.chainsconfig.api.ConfigDAO
import jp.co.soramitsu.xnetworking.lib.datasources.txhistory.api.models.ChainInfo
import jp.co.soramitsu.xnetworking.lib.datasources.txhistory.api.adapters.HistoryInfoRemoteLoader
import jp.co.soramitsu.xnetworking.lib.datasources.txhistory.api.models.TxFilter
import jp.co.soramitsu.xnetworking.lib.datasources.txhistory.api.models.TxHistoryInfo
import jp.co.soramitsu.xnetworking.lib.datasources.txhistory.api.models.TxHistoryItem
import jp.co.soramitsu.xnetworking.lib.datasources.txhistory.api.models.TxHistoryItemParam
import jp.co.soramitsu.xnetworking.lib.engines.rest.api.RestClient

class GiantSquidHistoryInfoRemoteLoader(
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

        val response = restClient.post(
            request = GiantSquidRequest(
                url = configDAO.historyUrl(chainInfo.chainId),
                address = signAddress
            )
        )

        val items = mutableListOf<TxHistoryItem>()

        if (TxFilter.TRANSFER in filters) {
            response.data.transfers?.map { transferWrapper ->
                TxHistoryItem(
                    id = transferWrapper.id,
                    blockHash = transferWrapper.transfer.blockNumber,
                    module = "transfer",
                    method = "",
                    timestamp = transferWrapper.transfer.timestamp,
                    nestedData = emptyList(),
                    networkFee = "",
                    success = transferWrapper.transfer.success,
                    data = listOfNotNull(
                        transferWrapper.transfer.to?.id?.let { to ->
                            TxHistoryItemParam(
                                "to",
                                to
                            )
                        },
                        transferWrapper.transfer.from?.id?.let { from ->
                            TxHistoryItemParam(
                                "from",
                                from
                            )
                        },
                        TxHistoryItemParam(
                            "amount",
                            transferWrapper.transfer.amount
                        ),
                        transferWrapper.transfer.extrinsicHash?.let { hash ->
                            TxHistoryItemParam(
                                "extrinsicHash",
                                hash
                            )
                        }
                    )
                )
            }?.toCollection(items)
        }

        if (TxFilter.REWARD in filters) {
            response.data.rewards?.map { reward ->
                TxHistoryItem(
                    id = reward.id,
                    blockHash = reward.blockNumber.toString(),
                    module = "reward",
                    method = "",
                    timestamp = reward.timestamp,
                    nestedData = emptyList(),
                    networkFee = "0",
                    success = true,
                    data = listOfNotNull(
                        TxHistoryItemParam(
                            "amount",
                            reward.amount
                        ),
                        reward.extrinsicHash?.let { extrinsicHash ->
                            TxHistoryItemParam(
                                "extrinsicHash",
                                extrinsicHash
                            )
                        },
                        reward.era?.let { era ->
                            TxHistoryItemParam(
                                "era",
                                era
                            )
                        },
                        reward.account?.id?.let { accountId ->
                            TxHistoryItemParam(
                                "accountId",
                                accountId
                            )
                        },
                        reward.validatorId?.let { validatorId ->
                            TxHistoryItemParam(
                                "validatorId",
                                validatorId
                            )
                        }
                    )
                )
            }?.toCollection(items)
        }

        if (TxFilter.EXTRINSIC in filters) {
            response.data.bonds?.map { bond ->
                TxHistoryItem(
                    id = bond.id,
                    blockHash = bond.blockNumber,
                    module = "bond",
                    method = "",
                    timestamp = bond.timestamp,
                    nestedData = emptyList(),
                    networkFee = "0",
                    success = bond.success ?: false,
                    data = listOfNotNull(
                        TxHistoryItemParam(
                            "amount",
                            bond.amount
                        ),
                        TxHistoryItemParam(
                            "accountId",
                            bond.accountId
                        ),
                        bond.extrinsicHash?.let { extrinsicHash ->
                            TxHistoryItemParam(
                                "extrinsicHash",
                                extrinsicHash
                            )
                        },
                        bond.type?.let { type ->
                            TxHistoryItemParam(
                                "type",
                                type
                            )
                        }
                    )
                )
            }?.toCollection(items)

            response.data.slashes?.map { slash ->
                TxHistoryItem(
                    id = slash.id,
                    blockHash = slash.blockNumber,
                    module = "slash",
                    method = "",
                    timestamp = slash.timestamp,
                    nestedData = emptyList(),
                    networkFee = "0",
                    success = true,
                    data = listOfNotNull(
                        TxHistoryItemParam(
                            "era",
                            slash.era
                        ),
                        TxHistoryItemParam(
                            "amount",
                            slash.amount
                        ),
                        TxHistoryItemParam(
                            "accountId",
                            slash.accountId
                        )
                    )
                )
            }?.toCollection(items)
        }

        return TxHistoryInfo(
            endCursor = null,
            endReached = true,
            items = items
        )
    }

}