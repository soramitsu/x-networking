package jp.co.soramitsu.commonnetworking.fearless

import jp.co.soramitsu.commonnetworking.networkclient.SoramitsuNetworkClient
import jp.co.soramitsu.commonnetworking.networkclient.SoramitsuNetworkException
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlin.coroutines.cancellation.CancellationException
import kotlin.math.min

class FearlessChainsBuilder(
    private val networkClient: SoramitsuNetworkClient,
    private val baseUrl: String,
) {

    companion object {
        private const val INDEX_URL = "index.json"
    }

    /**
     * @param version version of the app, e.g. 2.4.51
     * @param existedChains list of chains stored in the app, <id of chain, md5 hash of chain>
     */
    @Throws(SoramitsuNetworkException::class, ChainBuilderException::class, CancellationException::class)
    suspend fun getChains(
        version: String,
        existedChains: List<Pair<String, String>>
    ): ResultChainInfo {
        val indexResponse = networkClient.createJsonRequest<JsonElement>(baseUrl + INDEX_URL)
        val curPlatform = platform()
        val platform =
            runCatching { requireNotNull((indexResponse as JsonObject)[curPlatform]) }.getOrElse {
                throw PlatformNotFoundException("[$curPlatform] not found", null)
            }
        val curVersion = version.versionSplit()
        val chainList = runCatching {
            val selectedVersion = (platform as JsonArray).find { v ->
                val keysList = (v as JsonObject).keys.toList()
                keysList[0].versionSplit().match(curVersion)
            }
            Json.decodeFromJsonElement(
                ListSerializer(ChainResponse.serializer()),
                (selectedVersion as JsonObject).values.toList()[0]
            )
        }.getOrElse {
            throw VersionNotFoundException("[$curVersion] not found in [$curPlatform]", null)
        }
        val removedChains = existedChains.map { it.first }.minus(chainList.map { it.id })
        val updated = mutableListOf<ChainModel>()
        val new = mutableListOf<ChainModel>()
        chainList.forEach { chain ->
            val existed = existedChains.find { it.first == chain.id }
            if (existed == null) {
                val chainContent = networkClient.createRequest<String>(baseUrl + chain.chain)
                new.add(ChainModel(chainId = chain.id, hash = chain.hash, content = chainContent))
            } else {
                if (chain.hash != existed.second) {
                    val chainContent = networkClient.createRequest<String>(baseUrl + chain.chain)
                    updated.add(
                        ChainModel(
                            chainId = chain.id,
                            hash = chain.hash,
                            content = chainContent
                        )
                    )
                }
            }
        }
        return ResultChainInfo(new, updated, removedChains)
    }

    private fun List<Int>.match(cur: List<Int>): Boolean {
        val min = min(this.lastIndex, cur.lastIndex)
        for (i in 0..min) {
            if ((cur[i] > this[i]) || (cur[i] == this[i] && i == min)) return true
        }
        return false
    }

    private fun String.versionSplit(): List<Int> = this.split(".").map { it.toInt() }
}
