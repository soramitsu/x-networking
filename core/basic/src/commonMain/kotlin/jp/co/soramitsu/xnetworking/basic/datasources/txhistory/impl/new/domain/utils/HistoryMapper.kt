package jp.co.soramitsu.xnetworking.basic.datasources.txhistory.impl.new.domain.utils

import jp.co.soramitsu.xnetworking.basic.datasources.txhistory.api.models.TxHistoryItem
import jp.co.soramitsu.xnetworking.basic.datasources.txhistory.api.models.TxHistoryItemNested
import jp.co.soramitsu.xnetworking.basic.datasources.txhistory.api.models.TxHistoryItemParam
import jp.co.soramitsu.xnetworking.db.ExtrinsicParam
import jp.co.soramitsu.xnetworking.db.Extrinsics

object HistoryMapper {

    fun mapParams(extrinsic: Extrinsics, params: List<ExtrinsicParam>): TxHistoryItem =
        TxHistoryItem(
            id = extrinsic.txHash,
            blockHash = extrinsic.blockHash.orEmpty(),
            module = extrinsic.module,
            method = extrinsic.method,
            timestamp = extrinsic.timestamp.toString(),
            networkFee = extrinsic.networkFee,
            success = extrinsic.success,
            data = params.map { TxHistoryItemParam(it.paramName, it.paramValue) },
            nestedData = null,
        )

    fun mapItems(extrinsic: Extrinsics, params: List<TxHistoryItemNested>): TxHistoryItem =
        TxHistoryItem(
            id = extrinsic.txHash,
            blockHash = extrinsic.blockHash.orEmpty(),
            module = extrinsic.module,
            method = extrinsic.method,
            timestamp = extrinsic.timestamp.toString(),
            networkFee = extrinsic.networkFee,
            success = extrinsic.success,
            data = null,
            nestedData = params,
        )

    fun mapItemNested(extrinsic: Extrinsics, params: List<ExtrinsicParam>): TxHistoryItemNested =
        TxHistoryItemNested(
            module = extrinsic.module,
            method = extrinsic.method,
            hash = extrinsic.txHash,
            data = params.map { TxHistoryItemParam(it.paramName, it.paramValue) },
        )
}
