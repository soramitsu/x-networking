package jp.co.soramitsu.xnetworking.lib.datasources.txhistory.impl.domain.adapters.oklink

import jp.co.soramitsu.xnetworking.lib.datasources.chainsconfig.api.ConfigDAO
import jp.co.soramitsu.xnetworking.lib.datasources.txhistory.api.models.ChainInfo
import jp.co.soramitsu.xnetworking.lib.datasources.txhistory.api.adapters.HistoryInfoRemoteLoader
import jp.co.soramitsu.xnetworking.lib.datasources.txhistory.api.models.TxFilter
import jp.co.soramitsu.xnetworking.lib.datasources.txhistory.api.models.TxHistoryInfo
import jp.co.soramitsu.xnetworking.lib.datasources.txhistory.api.models.TxHistoryItem
import jp.co.soramitsu.xnetworking.lib.datasources.txhistory.api.models.TxHistoryItemParam
import jp.co.soramitsu.xnetworking.lib.engines.rest.api.RestClient

class OkLinkHistoryInfoRemoteLoader(
    private val apiKeys: Map<String, String>,
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
        require(chainInfo is ChainInfo.WithAssetSymbol)

        val response = restClient.get(
            request = OkLinkRequest(
                url = configDAO.historyUrl(chainInfo.chainId),
                address = signAddress,
                symbol = chainInfo.symbol,
                apiKey = checkNotNull(apiKeys[chainInfo.chainId]),
            )
        )

        check(response.code == 0) {
            "OkLink exception: code - ${response.code}, message - ${response.msg}."
        }

        val historyItems = response.data.firstOrNull()?.items ?: emptyList()

        return TxHistoryInfo(
            endCursor = null,
            endReached = true,
            items = historyItems.map {
                TxHistoryItem(
                    id = it.txId,
                    blockHash = it.blockHash,
                    module = "",
                    method = it.methodId,
                    timestamp = it.transactionTime.toString(),
                    nestedData = emptyList(),
                    networkFee = it.txFee,
                    success = it.state == "success",
                    data = listOf(
                        TxHistoryItemParam(
                            "height",
                            it.height
                        ),
                        TxHistoryItemParam(
                            "tokenId",
                            it.tokenId
                        ),
                        TxHistoryItemParam(
                            "amount",
                            it.amount
                        ),
                        TxHistoryItemParam(
                            "to",
                            it.to
                        ),
                        TxHistoryItemParam(
                            "isToContract",
                            it.isToContract.toString()
                        ),
                        TxHistoryItemParam(
                            "from",
                            it.from
                        ),
                        TxHistoryItemParam(
                            "isFromContract",
                            it.isFromContract.toString()
                        ),
                        TxHistoryItemParam(
                            "originHash",
                            it.l1OriginHash
                        ),
                        TxHistoryItemParam(
                            "transactionSymbol",
                            it.transactionSymbol
                        ),
                        TxHistoryItemParam(
                            "contractAddress",
                            it.tokenContractAddress
                        ),
                        TxHistoryItemParam(
                            "challengeStatus",
                            it.challengeStatus
                        ),
                        TxHistoryItemParam(
                            "state",
                            it.state
                        )
                    )
                )
            }
        )
    }

}

