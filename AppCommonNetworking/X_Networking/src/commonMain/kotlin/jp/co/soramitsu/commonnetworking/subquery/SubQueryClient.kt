package jp.co.soramitsu.commonnetworking.subquery

import io.ktor.http.*
import jp.co.soramitsu.commonnetworking.db.Extrinsics
import jp.co.soramitsu.commonnetworking.db.SignerInfo
import jp.co.soramitsu.commonnetworking.dbengine.HistoryDatabaseProvider
import jp.co.soramitsu.commonnetworking.networkclient.SoramitsuNetworkClient
import jp.co.soramitsu.commonnetworking.networkclient.SoramitsuNetworkException
import jp.co.soramitsu.commonnetworking.subquery.graphql.SubQueryRequest
import jp.co.soramitsu.commonnetworking.subquery.graphql.referrerRewardsGraphQLRequest
import jp.co.soramitsu.commonnetworking.subquery.graphql.sbApyGraphQLRequest
import jp.co.soramitsu.commonnetworking.subquery.graphql.soraHistoryGraphQLVariables
import jp.co.soramitsu.commonnetworking.subquery.history.HistoryMapper
import jp.co.soramitsu.commonnetworking.subquery.history.SubQueryHistoryInfo
import jp.co.soramitsu.commonnetworking.subquery.history.SubQueryHistoryItem
import jp.co.soramitsu.commonnetworking.subquery.history.SubQueryHistoryResult
import jp.co.soramitsu.commonnetworking.subquery.response.ReferrerRewardsResponse
import jp.co.soramitsu.commonnetworking.subquery.response.SubQuerySbApyResponse
import kotlinx.serialization.DeserializationStrategy
import kotlin.coroutines.cancellation.CancellationException
import kotlin.math.max
import kotlin.math.min

class SubQueryClient<T, R> internal constructor(
    private val networkClient: SoramitsuNetworkClient,
    private var baseUrl: String,
    private val pageSize: Int,
    private val deserializationStrategy: DeserializationStrategy<T>,
    private val jsonToHistoryInfo: (T) -> SubQueryHistoryInfo,
    private val historyInfoToResult: (SubQueryHistoryItem) -> R,
    private val historyRequest: String,
    historyDatabaseProvider: HistoryDatabaseProvider
) {
    private val historyDatabase = historyDatabaseProvider.provide()

    fun clearAllData() {
        historyDatabase.clearDatabase()
    }

    fun clearData(address: String, networkName: String) {
        historyDatabase.clearAddressData(address, networkName)
    }

    @Throws(SoramitsuNetworkException::class, CancellationException::class)
    suspend fun getSpApy(
        url: String = baseUrl,
    ): List<SbApyInfo> {
        val response = networkClient.createJsonRequest<SubQuerySbApyResponse>(
            url,
            HttpMethod.Post,
            SubQueryRequest(sbApyGraphQLRequest())
        )
        return SubQueryMapper.map(response)
    }

    @Throws(SoramitsuNetworkException::class, CancellationException::class)
    suspend fun getReferrerRewards(
        address: String,
        url: String = baseUrl,
    ): ReferrerRewardsInfo {
        var cursor = ""
        val list = mutableListOf<ReferrerReward>()
        while (true) {
            val response = networkClient.createJsonRequest<ReferrerRewardsResponse>(
                url,
                HttpMethod.Post,
                SubQueryRequest(referrerRewardsGraphQLRequest(address, cursor))
            )
            list.addAll(response.data.referrerRewards.nodes.map {
                ReferrerReward(
                    referral = it.referral,
                    amount = it.amount,
                )
            })
            if (response.data.referrerRewards.pageInfo.hasNextPage && response.data.referrerRewards.pageInfo.endCursor != null) {
                cursor = response.data.referrerRewards.pageInfo.endCursor
            } else {
                break
            }
        }
        return ReferrerRewardsInfo(list)
    }

    fun getTransactionPeers(query: String): List<String> =
        historyDatabase.getTransfersAddress(query)

    fun getTransactionHistoryCached(
        address: String,
        networkName: String,
    ): SubQueryHistoryInfo {
        val extrinsics = historyDatabase.getExtrinsics(address, networkName, 0, pageSize)
        return buildResultHistoryInfo(true, extrinsics)
    }

    fun getTransactionCached(
        address: String,
        networkName: String,
        txHash: String,
    ): SubQueryHistoryInfo {
        val extrinsic = historyDatabase.getExtrinsic(address, networkName, txHash)
        return buildResultHistoryInfo(
            true,
            if (extrinsic == null) emptyList() else listOf(extrinsic)
        )
    }

    private lateinit var curSignerInfo: SignerInfo

    @Throws(
        SoramitsuNetworkException::class,
        CancellationException::class,
        IllegalArgumentException::class
    )
    suspend fun getTransactionHistoryPaged(
        address: String,
        networkName: String,
        page: Long,
        url: String? = null,
        filter: ((R) -> Boolean)? = null
    ): SubQueryHistoryResult<R> {
        require(page >= 1) { "Page value must >= 1" }
        if (url != null) baseUrl = url
        curSignerInfo = historyDatabase.getSignerInfo(address, networkName)
        if (page == 1L) {
            loadInfo(address = address, networkName = networkName)
        }
        var curPage = page
        val list = mutableListOf<R>()
        var endCursor: String?
        var endReached: Boolean
        while (true) {
            val info = getHistoryInfo(curPage, address, networkName)
            endCursor = info.endCursor
            endReached = info.endReached
            val itemsMapped = info.items.map(historyInfoToResult)
            val itemsFiltered = if (filter != null) itemsMapped.filter {
                filter.invoke(it)
            } else itemsMapped
            list.addAll(itemsFiltered)
            if (list.size >= pageSize || info.endReached) {
                break
            } else {
                curPage++
            }
        }
        return SubQueryHistoryResult(
            endCursor = endCursor,
            endReached = endReached,
            page = curPage,
            items = list
        )
    }

    private suspend fun getHistoryInfo(
        page: Long,
        address: String,
        networkName: String,
    ): SubQueryHistoryInfo {
        var dbOffset = (page - 1) * pageSize
        val extrinsics = historyDatabase.getExtrinsics(address, networkName, dbOffset, pageSize)
        dbOffset += extrinsics.size
        return if (extrinsics.size.toLong() >= pageSize) {
            buildResultHistoryInfo(false, extrinsics)
        } else {
            if (curSignerInfo.endReached) {
                buildResultHistoryInfo(true, extrinsics)
            } else {
                loadInfo(curSignerInfo.oldCursor.orEmpty(), address, networkName)
                val moreCount = pageSize - extrinsics.size
                val moreExtrinsics =
                    historyDatabase.getExtrinsics(address, networkName, dbOffset, moreCount)
                dbOffset += moreExtrinsics.size
                buildResultHistoryInfo(curSignerInfo.endReached, extrinsics + moreExtrinsics)
            }
        }
    }

    private suspend fun loadInfo(cursor: String = "", address: String, networkName: String) {
        val request = SubQueryRequest(
            query = historyRequest,
            variables = soraHistoryGraphQLVariables(
                countRemote = pageSize,
                myAddress = address,
                cursor = cursor,
            )
        )
        val response = networkClient.createRequest<String>(
            baseUrl,
            HttpMethod.Post,
            request,
            ContentType.Application.Json
        )
        val decoded = networkClient.json.decodeFromString(deserializationStrategy, response)
        val infoDecoded = jsonToHistoryInfo.invoke(decoded)
        val info =
            historyDatabase.insertExtrinsics(address, networkName, infoDecoded)
        curSignerInfo = SignerInfo(
            signAddress = address,
            networkName = networkName,
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
    ): SubQueryHistoryInfo {
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
        return SubQueryHistoryInfo("", endReached, mapped)
    }
}
