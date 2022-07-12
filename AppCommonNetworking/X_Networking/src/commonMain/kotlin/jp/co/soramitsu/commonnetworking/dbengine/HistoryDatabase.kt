package jp.co.soramitsu.commonnetworking.dbengine

import jp.co.soramitsu.commonnetworking.db.ExtrinsicParam
import jp.co.soramitsu.commonnetworking.db.Extrinsics
import jp.co.soramitsu.commonnetworking.db.SignerInfo
import jp.co.soramitsu.commonnetworking.db.SoraHistoryDatabase
import jp.co.soramitsu.commonnetworking.subquery.history.SubQueryHistoryInfo

internal class HistoryDatabase(historyDatabaseFactory: DatabaseDriverFactory) {
    private val db = SoraHistoryDatabase(historyDatabaseFactory.createDriver())
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

    internal fun getTransfersAddress(query: String): List<String> {
        return dbQuery.selectTransfersPeers(query).executeAsList()
    }

    internal fun getSignerInfo(signAddress: String, networkName: String): SignerInfo =
        dbQuery.selectSignerInfo(signAddress, networkName).executeAsOneOrNull() ?: SignerInfo(
            signAddress,
            networkName,
            0,
            0,
            null,
            false
        )

    internal fun insertSignerInfo(info: SignerInfo) = dbQuery.insertSignerInfoFull(info)

    internal fun getExtrinsics(signAddress: String, networkName: String, offset: Long, count: Int): List<Extrinsics> {
        return dbQuery.selectExtrinsicsPaged(signAddress, networkName, count.toLong(), offset).executeAsList()
    }

    internal fun getExtrinsic(signAddress: String, networkName: String, txHash: String): Extrinsics? {
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
        response: SubQueryHistoryInfo,
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