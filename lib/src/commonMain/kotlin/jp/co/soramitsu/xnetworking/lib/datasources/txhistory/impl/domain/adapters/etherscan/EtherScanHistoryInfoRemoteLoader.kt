package jp.co.soramitsu.xnetworking.lib.datasources.txhistory.impl.domain.adapters.etherscan

import jp.co.soramitsu.xnetworking.lib.datasources.chainsconfig.api.ConfigDAO
import jp.co.soramitsu.xnetworking.lib.datasources.txhistory.api.models.ChainInfo
import jp.co.soramitsu.xnetworking.lib.datasources.txhistory.api.adapters.HistoryInfoRemoteLoader
import jp.co.soramitsu.xnetworking.lib.datasources.txhistory.api.models.TxFilter
import jp.co.soramitsu.xnetworking.lib.datasources.txhistory.api.models.TxHistoryInfo
import jp.co.soramitsu.xnetworking.lib.datasources.txhistory.api.models.TxHistoryItem
import jp.co.soramitsu.xnetworking.lib.datasources.txhistory.api.models.TxHistoryItemParam
import jp.co.soramitsu.xnetworking.lib.engines.rest.api.RestClient

class EtherScanHistoryInfoRemoteLoader(
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
        require(chainInfo is ChainInfo.WithEthereumType) {
            "EtherScan blockExplorer can not be used with non-ethereum chains."
        }

        val response = restClient.get(
            request = when(chainInfo.ethereumType) {
                "normal" -> NormalEtherScanRequest(
                    url = configDAO.historyUrl(chainInfo.chainId),
                    address = signAddress,
                    apiKey = requireNotNull(
                        apiKeys[chainInfo.chainId]
                    ) { "Api key for etherScan chain with id - ${chainInfo.chainId} - is not set." }
                )

                "erc20", "bep20" -> ErcBepEtherScanRequest(
                    url = configDAO.historyUrl(chainInfo.chainId),
                    contractAddress = chainInfo.contractAddress,
                    address = signAddress,
                    apiKey = requireNotNull(
                        apiKeys[chainInfo.chainId]
                    ) { "Api key for etherScan chain with id - ${chainInfo.chainId} - is not set." }
                )

                else -> error("Unknown AssetType for EtherScan BlockExplorer")
            }
        )

        return TxHistoryInfo(
            endCursor = null,
            endReached = true,
            items = response.result.map { historyElement ->
                TxHistoryItem(
                    id = historyElement.hash,
                    blockHash = historyElement.blockHash,
                    module = "",
                    method = "",
                    timestamp = historyElement.timeStamp.toString(),
                    nestedData = emptyList(),
                    networkFee = "0", // fee for Eth network is calculated differently between transactions
                    success = response.status == 1 && historyElement.isError == 0,
                    data = listOf(
                        TxHistoryItemParam(
                            "blockNumber",
                            historyElement.blockNumber
                        ),
                        TxHistoryItemParam(
                            "nonce",
                            historyElement.nonce
                        ),
                        TxHistoryItemParam(
                            "amount",
                            historyElement.value
                        ),
                        TxHistoryItemParam(
                            "to",
                            historyElement.to
                        ),
                        TxHistoryItemParam(
                            "from",
                            historyElement.from
                        ),
                        TxHistoryItemParam(
                            "gas",
                            historyElement.gas
                        ),
                        TxHistoryItemParam(
                            "gasPrice",
                            historyElement.gasPrice
                        ),
                        TxHistoryItemParam(
                            "gasUsed",
                            historyElement.gasUsed
                        ),
                        TxHistoryItemParam(
                            "contractAddress",
                            historyElement.contractAddress
                        )
                    )
                )
            }
        )
    }

}