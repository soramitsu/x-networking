package jp.co.soramitsu.xnetworking.basic.datasources.txhistory.impl.new

import jp.co.soramitsu.xnetworking.basic.datasources.txhistory.api.models.TxHistoryInfo
import jp.co.soramitsu.xnetworking.db.ExtrinsicParam
import jp.co.soramitsu.xnetworking.db.Extrinsics
import jp.co.soramitsu.xnetworking.db.SignerInfo
import jp.co.soramitsu.xnetworking.db.SoraHistoryDatabase

internal class SoraHistoryDBImpl(
    private val soraHistoryDatabase: SoraHistoryDatabase
) {

    private val dbQuery = soraHistoryDatabase.soraHistoryDatabaseQueries

    fun getTransfersAddress(
        query: String,
        networkName: String
    ): List<String> {
        return dbQuery.selectTransfersPeers(networkName, query).executeAsList()
    }

    fun getExtrinsicParams(
        extrinsicHash: String
    ): List<ExtrinsicParam> {
        return dbQuery.selectExtrinsicParams(extrinsicHash).executeAsList()
    }

    fun getExtrinsics(
        signAddress: String,
        networkName: String,
        offset: Long,
        count: Int
    ): List<Extrinsics> {
        return dbQuery.selectExtrinsicsPaged(signAddress, networkName, count.toLong(), offset)
            .executeAsList()
    }

    fun getExtrinsic(
        signAddress: String,
        networkName: String,
        txHash: String
    ): Extrinsics? {
        return dbQuery.selectExtrinsic(txHash, signAddress, networkName).executeAsOneOrNull()
    }

    fun getExtrinsicNested(
        extrinsicHash: String
    ): List<Extrinsics> {
        return dbQuery.selectExtrinsicsNested(extrinsicHash).executeAsList()
    }

    fun insertExtrinsics(
        signAddress: String,
        networkName: String,
        response: TxHistoryInfo
    ): SignerInfo {
        dbQuery.transaction {
            response.items.forEach { historyResponseItem ->
                val time = historyResponseItem.timestamp.toLong()
                dbQuery.insertExtrinsic(
                    txHash = historyResponseItem.id,
                    signAddress = signAddress,
                    networkName = networkName,
                    blockHash = historyResponseItem.blockHash,
                    module = historyResponseItem.module,
                    method = historyResponseItem.method,
                    timestamp = time,
                    networkFee = historyResponseItem.networkFee,
                    success = historyResponseItem.success,
                    batch = historyResponseItem.nestedData != null,
                    parentHash = null,
                )
                if (historyResponseItem.data != null) {
                    historyResponseItem.data.forEach {
                        dbQuery.insertExtrinsicParam(
                            historyResponseItem.id,
                            it.paramName,
                            it.paramValue
                        )
                    }
                }
                if (historyResponseItem.nestedData != null) {
                    historyResponseItem.nestedData.forEach { itemNested ->
                        val hash = itemNested.hash
                        dbQuery.insertExtrinsic(
                            txHash = hash,
                            signAddress = signAddress,
                            networkName = networkName,
                            blockHash = historyResponseItem.blockHash,
                            module = itemNested.module,
                            method = itemNested.method,
                            timestamp = time,
                            networkFee = "",
                            success = historyResponseItem.success,
                            batch = false,
                            parentHash = historyResponseItem.id
                        )
                        itemNested.data.forEach { nested ->
                            dbQuery.insertExtrinsicParam(
                                hash,
                                nested.paramName,
                                nested.paramValue,
                            )
                        }
                    }
                }
            }
        }
        return SignerInfo(
            signAddress = signAddress,
            networkName = networkName,
            topTime = response.items.firstOrNull()?.timestamp?.toLong() ?: 0,
            oldTime = response.items.lastOrNull()?.timestamp?.toLong() ?: 0,
            oldCursor = response.endCursor,
            endReached = response.endReached,
        )
    }

    fun getSignerInfo(
        signAddress: String,
        networkName: String
    ): SignerInfo =
        dbQuery.selectSignerInfo(signAddress, networkName).executeAsOneOrNull() ?: SignerInfo(
            signAddress,
            0,
            0,
            null,
            false,
            networkName,
        )

    fun insertSignerInfo(
        info: SignerInfo
    ) = dbQuery.insertSignerInfoFull(info)

    fun clearAddressData(
        signAddress: String,
        networkName: String
    ) {
        dbQuery.transaction {
            dbQuery.removeExtrinsics(signAddress, networkName)
            dbQuery.removeSignerInfo(signAddress, networkName)
        }
    }

    fun clearDatabase() {
        dbQuery.transaction {
            dbQuery.removeAllExtrinsics()
            dbQuery.removeAllSignerInfo()
        }
    }
}