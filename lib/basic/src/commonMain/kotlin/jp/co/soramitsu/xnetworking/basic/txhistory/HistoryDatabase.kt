package jp.co.soramitsu.xnetworking.basic.txhistory

import jp.co.soramitsu.xnetworking.basic.dbengine.DatabaseDriverFactory
import jp.co.soramitsu.xnetworking.db.ExtrinsicParam
import jp.co.soramitsu.xnetworking.db.Extrinsics
import jp.co.soramitsu.xnetworking.db.SignerInfo
import jp.co.soramitsu.xnetworking.db.SoraHistoryDatabase

class HistoryDatabaseProvider(private val databaseDriverFactory: DatabaseDriverFactory) {
    internal fun provide(): HistoryDatabase {
        return HistoryDatabase(databaseDriverFactory)
    }
}

internal class HistoryDatabase(databaseDriverFactory: DatabaseDriverFactory) {
    private val db = SoraHistoryDatabase(databaseDriverFactory.createDriver())
    private val dbQuery = db.soraHistoryDatabaseQueries

    internal fun clearDatabase() {
        dbQuery.transaction {
            dbQuery.removeAllExtrinsics()
            dbQuery.removeAllSignerInfo()
        }
    }

    internal fun clearAddressData(signAddress: String, networkName: String) {
        dbQuery.transaction {
            dbQuery.removeExtrinsics(signAddress, networkName)
            dbQuery.removeSignerInfo(signAddress, networkName)
        }
    }

    internal fun getTransfersAddress(query: String, networkName: String): List<String> {
        return dbQuery.selectTransfersPeers(networkName, query).executeAsList()
    }

    internal fun getSignerInfo(signAddress: String, networkName: String): SignerInfo =
        dbQuery.selectSignerInfo(signAddress, networkName).executeAsOneOrNull() ?: SignerInfo(
            signAddress,
            0,
            0,
            null,
            false,
            networkName,
        )

    internal fun insertSignerInfo(info: SignerInfo) = dbQuery.insertSignerInfoFull(info)

    internal fun getExtrinsics(
        signAddress: String,
        networkName: String,
        offset: Long,
        count: Int
    ): List<Extrinsics> {
        return dbQuery.selectExtrinsicsPaged(signAddress, networkName, count.toLong(), offset)
            .executeAsList()
    }

    internal fun getExtrinsic(
        signAddress: String,
        networkName: String,
        txHash: String
    ): Extrinsics? {
        return dbQuery.selectExtrinsic(txHash, signAddress, networkName).executeAsOneOrNull()
    }

    internal fun getExtrinsicParams(extrinsicHash: String): List<ExtrinsicParam> {
        return dbQuery.selectExtrinsicParams(extrinsicHash).executeAsList()
    }

    internal fun getExtrinsicNested(extrinsicHash: String): List<Extrinsics> {
        return dbQuery.selectExtrinsicsNested(extrinsicHash).executeAsList()
    }

    internal fun insertExtrinsics(
        signAddress: String,
        networkName: String,
        response: TxHistoryInfo,
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
}
