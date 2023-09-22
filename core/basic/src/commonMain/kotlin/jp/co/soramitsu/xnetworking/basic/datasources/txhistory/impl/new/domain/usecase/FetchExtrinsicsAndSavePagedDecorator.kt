package jp.co.soramitsu.xnetworking.basic.datasources.txhistory.impl.new.domain.usecase

import jp.co.soramitsu.xnetworking.basic.datasources.txhistory.impl.new.SoraHistoryDBImpl
import jp.co.soramitsu.xnetworking.db.Extrinsics
import jp.co.soramitsu.xnetworking.db.SignerInfo

internal class FetchExtrinsicsAndSavePagedDecorator(
    private val fetchExtrinsicsAndSaveUseCase: FetchExtrinsicsAndSaveUseCase,
    private val soraHistoryDBImpl: SoraHistoryDBImpl,
) {

    class PagedExtrinsicsWrapper(
        val isEndReached: Boolean,
        val items: List<Extrinsics>
    )

    suspend operator fun <T> invoke(
        signAddress: String,
        networkName: String,
        page: Long,
        currentSignerInfo: SignerInfo,
        extrinsicsCountPerPage: Int,
    ): PagedExtrinsicsWrapper {
        var dbOffset = (page - 1) * extrinsicsCountPerPage

        val extrinsics = soraHistoryDBImpl.getExtrinsics(
            signAddress = signAddress,
            networkName = networkName,
            offset = dbOffset,
            count = extrinsicsCountPerPage
        )

        dbOffset += extrinsics.size

        if (extrinsics.size.toLong() >= extrinsicsCountPerPage) {
            return PagedExtrinsicsWrapper(
                isEndReached = false,
                items = extrinsics
            )
        } else {
            if (currentSignerInfo.endReached) {
                return PagedExtrinsicsWrapper(
                    isEndReached = true,
                    items = extrinsics
                )
            } else {
                val curSignerInfo = fetchExtrinsicsAndSaveUseCase<T>(
                    cursor = currentSignerInfo.oldCursor.orEmpty(),
                    signAddress = signAddress,
                    networkName = networkName,
                    currentSignerInfo = currentSignerInfo,
                )

                val extrinsicsRemainder = extrinsicsCountPerPage - extrinsics.size

                val moreExtrinsics = soraHistoryDBImpl.getExtrinsics(
                    signAddress = signAddress,
                    networkName = networkName,
                    offset = dbOffset,
                    count = extrinsicsRemainder
                )
                
                dbOffset += moreExtrinsics.size

                return PagedExtrinsicsWrapper(
                    isEndReached = curSignerInfo.endReached,
                    items = extrinsics.plus(moreExtrinsics)
                )
            }
        }
    }

}