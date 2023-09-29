package jp.co.soramitsu.xnetworking.sorawallet.common.interactors.blockexplorer.api

import jp.co.soramitsu.xnetworking.basic.networkclient.SoramitsuNetworkException
import jp.co.soramitsu.xnetworking.sorawallet.core.datasources.blockexplorer.api.models.AssetsInfoResponse
import jp.co.soramitsu.xnetworking.sorawallet.core.datasources.blockexplorer.api.models.FiatDataResponse
import jp.co.soramitsu.xnetworking.sorawallet.core.datasources.blockexplorer.api.models.ReferrerRewardResponse
import jp.co.soramitsu.xnetworking.sorawallet.core.datasources.blockexplorer.api.models.SbApyInfoResponse
import kotlin.coroutines.cancellation.CancellationException

interface BlockExplorerInteractor {

    @Throws(
        SoramitsuNetworkException::class,
        CancellationException::class,
        IllegalArgumentException::class
    )
    suspend fun getAssetsInfo(
        tokenIds: List<String>,
        timeStamp: Int
    ): List<AssetsInfoResponse>

    @Throws(
        SoramitsuNetworkException::class,
        CancellationException::class,
        IllegalArgumentException::class
    )
    suspend fun getFiat(): List<FiatDataResponse>

    @Throws(
        SoramitsuNetworkException::class,
        CancellationException::class,
        IllegalArgumentException::class
    )
    suspend fun getReferrerRewards(
        address: String
    ): List<ReferrerRewardResponse>

    @Throws(
        SoramitsuNetworkException::class,
        CancellationException::class,
        IllegalArgumentException::class
    )
    suspend fun getSbApyInfo(): List<SbApyInfoResponse>
}