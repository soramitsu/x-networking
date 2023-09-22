package jp.co.soramitsu.xnetworking.basic.datasources.txhistory.impl.new.domain.usecase

import jp.co.soramitsu.xnetworking.basic.datasources.txhistory.api.HistoryInfoRemoteLoader
import jp.co.soramitsu.xnetworking.basic.datasources.txhistory.impl.new.SoraHistoryDBImpl
import jp.co.soramitsu.xnetworking.db.SignerInfo
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sign

internal class FetchExtrinsicsAndSaveUseCase(
    private val soraHistoryDBImpl: SoraHistoryDBImpl,
    private val historyInfoRemoteLoader: HistoryInfoRemoteLoader
) {

    suspend operator fun <T> invoke(
        cursor: String,
        signAddress: String,
        networkName: String,
        currentSignerInfo: SignerInfo,
    ): SignerInfo {
        val txHistoryInfo = soraHistoryDBImpl.insertExtrinsics(
            signAddress,
            networkName,
            historyInfoRemoteLoader.loadHistoryInfo(
                pageCount = 100,
                cursor = cursor,
                signAddress = signAddress
            )
        )

        val isCurrentSignerTimestampIsInvalid =
            currentSignerInfo.oldTime == 0L

        val isFinalEndReached = txHistoryInfo.endReached
            || currentSignerInfo.endReached

        val isCurrentSignerCursorInvalid =
            txHistoryInfo.endReached ||
            currentSignerInfo.oldCursor.isNullOrEmpty() ||
            txHistoryInfo.oldTime < currentSignerInfo.oldTime

        return SignerInfo(
            signAddress = signAddress,
            networkName = networkName,
            topTime = max(txHistoryInfo.topTime, currentSignerInfo.topTime),
            oldTime = if (isCurrentSignerTimestampIsInvalid)
                txHistoryInfo.oldTime else min(txHistoryInfo.oldTime, currentSignerInfo.oldTime),
            endReached = isFinalEndReached,
            oldCursor = if (isCurrentSignerCursorInvalid)
                txHistoryInfo.oldCursor else currentSignerInfo.oldCursor
        ).apply { soraHistoryDBImpl.insertSignerInfo(this) }
    }

}