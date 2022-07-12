package jp.co.soramitsu.commonnetworking.subquery.history

import jp.co.soramitsu.commonnetworking.db.ExtrinsicParam
import jp.co.soramitsu.commonnetworking.db.Extrinsics

object HistoryMapper {

    fun mapParams(extrinsic: Extrinsics, params: List<ExtrinsicParam>): SubQueryHistoryItem =
        SubQueryHistoryItem(
            id = extrinsic.txHash,
            blockHash = extrinsic.blockHash.orEmpty(),
            module = extrinsic.module,
            method = extrinsic.method,
            timestamp = extrinsic.timestamp.toString(),
            networkFee = extrinsic.networkFee,
            success = extrinsic.success,
            data = params.map { SubQueryHistoryItemParam(it.paramName, it.paramValue) },
            nestedData = null,
        )

    fun mapItems(extrinsic: Extrinsics, params: List<SubQueryHistoryItemNested>): SubQueryHistoryItem =
        SubQueryHistoryItem(
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

    fun mapItemNested(extrinsic: Extrinsics, params: List<ExtrinsicParam>): SubQueryHistoryItemNested =
        SubQueryHistoryItemNested(
            module = extrinsic.module,
            method = extrinsic.method,
            hash = extrinsic.txHash,
            data = params.map { SubQueryHistoryItemParam(it.paramName, it.paramValue) },
        )
}
