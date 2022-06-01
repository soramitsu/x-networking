package jp.co.soramitsu.commonnetworking.subquery

import io.ktor.http.HttpMethod
import jp.co.soramitsu.commonnetworking.db.Extrinsics
import jp.co.soramitsu.commonnetworking.db.SignerInfo
import jp.co.soramitsu.commonnetworking.dbengine.HistoryDatabaseProvider
import jp.co.soramitsu.commonnetworking.networkclient.SoramitsuNetworkClient
import jp.co.soramitsu.commonnetworking.networkclient.SoramitsuNetworkException
import jp.co.soramitsu.commonnetworking.subquery.graphql.SubQueryRequest
import jp.co.soramitsu.commonnetworking.subquery.graphql.referrerRewardsGraphQLRequest
import jp.co.soramitsu.commonnetworking.subquery.graphql.sbApyGraphQLRequest
import jp.co.soramitsu.commonnetworking.subquery.graphql.soraHistoryGraphQLRequest
import jp.co.soramitsu.commonnetworking.subquery.history.HistoryMapper
import jp.co.soramitsu.commonnetworking.subquery.history.SoraHistoryInfo
import jp.co.soramitsu.commonnetworking.subquery.history.SoraSubqueryResponse
import kotlin.coroutines.cancellation.CancellationException
import kotlin.math.max
import kotlin.math.min
import kotlinx.coroutines.*

class SubQueryClient internal constructor(
    private val networkClient: SoramitsuNetworkClient,
    private val baseUrl: String,
    private val pageSize: Int,
    historyDatabaseProvider: HistoryDatabaseProvider
) {
    private val historyDatabase = historyDatabaseProvider.provide()

    fun clearAllData() {
        historyDatabase.clearDatabase()
    }

    fun clearData(address: String) {
        historyDatabase.clearAddressData(address)
    }

    @Throws(SoramitsuNetworkException::class, CancellationException::class)
    suspend fun getSpApy(): List<SbApyInfo> {
        val response = networkClient.createJsonRequest<SubQuerySbApyResponse>(
            baseUrl,
            HttpMethod.Post,
            SubQueryRequest(sbApyGraphQLRequest())
        )
        return SubQueryMapper.map(response)
    }

    @Throws(SoramitsuNetworkException::class, CancellationException::class)
    suspend fun getReferrerRewards(
        address: String,
    ): ReferrerRewardsInfo {
        val response = networkClient.createJsonRequest<ReferrerRewardsResponse>(
            baseUrl,
            HttpMethod.Post,
            SubQueryRequest(referrerRewardsGraphQLRequest(address))
        )
        return if (response.data.referrerRewards.groupedAggregates.isEmpty()) {
            ReferrerRewardsInfo(rewards = emptyList())
        } else {
            ReferrerRewardsInfo(rewards = response.data.referrerRewards.groupedAggregates.map {
                ReferrerRewards(
                    referral = it.keys.firstOrNull().orEmpty(),
                    amount = it.sum.amount
                )
            })
        }
    }

    private var historyAddress: String = ""
    private lateinit var curSignerInfo: SignerInfo

    fun getTransactionPeers(query: String): List<String> = historyDatabase.getTransfersAddress(query)

    fun getTransactionHistoryCached(
        address: String,
    ): SoraHistoryInfo {
        val extrinsics = historyDatabase.getExtrinsics(historyAddress, 0, pageSize)
        return buildResultHistoryInfo(true, extrinsics)
    }

    fun getTransactionCached(
        address: String,
        txHash: String,
    ): SoraHistoryInfo {
        val extrinsic = historyDatabase.getExtrinsic(address, txHash)
        return buildResultHistoryInfo(true, if (extrinsic == null) emptyList() else listOf(extrinsic))
    }

    @Throws(SoramitsuNetworkException::class, CancellationException::class, IllegalArgumentException::class)
    suspend fun getTransactionHistoryPaged(
        address: String,
        page: Long,
    ): SoraHistoryInfo {

        withContext(Dispatchers.Default) {
            require(((page > 1 && historyAddress.isEmpty()) || page < 1).not()) { "First page value must = 1" }
            if (page == 1L) {


            }
            historyAddress = address
            curSignerInfo = historyDatabase.getSignerInfo(historyAddress)
            loadInfo()
        }

        return withContext(Dispatchers.Default) {
            getHistoryInfo(page)
        }
    }

    private suspend fun getHistoryInfo(page: Long): SoraHistoryInfo {
        var dbOffset = (page - 1) * pageSize
        val extrinsics = historyDatabase.getExtrinsics(historyAddress, dbOffset, pageSize)
        dbOffset += extrinsics.size
        return if (extrinsics.size.toLong() >= pageSize) {
            buildResultHistoryInfo(false, extrinsics)
        } else {
            if (curSignerInfo.endReached) {
                buildResultHistoryInfo(true, extrinsics)
            } else {
                loadInfo(curSignerInfo.oldCursor.orEmpty())
                val moreCount = pageSize - extrinsics.size
                val moreExtrinsics =
                    historyDatabase.getExtrinsics(historyAddress, dbOffset, moreCount)
                dbOffset += moreExtrinsics.size
                buildResultHistoryInfo(curSignerInfo.endReached, extrinsics + moreExtrinsics)
            }
        }
    }

    private suspend fun loadInfo(cursor: String = "") {
        val response = networkClient.createJsonRequest<SoraSubqueryResponse>(
            baseUrl,
            HttpMethod.Post,
            SubQueryRequest(
                soraHistoryGraphQLRequest(
                    pageSize,
                    historyAddress,
                    cursor
                )
            )
        )
        val info = historyDatabase.insertExtrinsics(historyAddress, response)
        curSignerInfo = SignerInfo(
            signAddress = historyAddress,
            topTime = max(info.topTime, curSignerInfo.topTime),
            oldTime = if (curSignerInfo.oldTime == 0L) info.oldTime else min(
                info.oldTime,
                curSignerInfo.oldTime
            ),
            endReached = info.endReached || curSignerInfo.endReached,
            oldCursor = if (info.endReached || curSignerInfo.oldCursor.isNullOrEmpty() || info.oldTime < curSignerInfo.oldTime) info.oldCursor else curSignerInfo.oldCursor
        )
        historyDatabase.insertSignerInfo(curSignerInfo)
    }

    private fun buildResultHistoryInfo(
        endReached: Boolean,
        extrinsics: List<Extrinsics>
    ): SoraHistoryInfo {
        val mapped = extrinsics.map { extrinsic ->
            if (extrinsic.batch.not()) {
                val params = historyDatabase.getExtrinsicParams(extrinsic.txHash)
                HistoryMapper.mapParams(extrinsic, params)
            } else {
                val nestedExtrinsics = historyDatabase.getExtrinsicNested(extrinsic.txHash)
                val nestedMapped = nestedExtrinsics.map {
                    val nestedParams = historyDatabase.getExtrinsicParams(it.txHash)
                    HistoryMapper.mapItemNested(it, nestedParams)
                }
                HistoryMapper.mapItems(extrinsic, nestedMapped)
            }
        }
        return SoraHistoryInfo(endReached, mapped)
    }
}
