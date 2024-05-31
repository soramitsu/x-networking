package jp.co.soramitsu.xnetworking.fearlesswallet.chainbuilder

import jp.co.soramitsu.xnetworking.basic.networkclient.SoramitsuNetworkClient
import jp.co.soramitsu.xnetworking.basic.networkclient.SoramitsuNetworkException
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlin.coroutines.cancellation.CancellationException
import kotlin.math.min

class FearlessChainsBuilder(
    private val networkClient: SoramitsuNetworkClient,
    private val baseUrl: String,
    private val indexFile: String,
) {

    /**
     * @param version version of the app, e.g. 2.4.51
     * @param existedChains list of chains stored in the app, <id of chain, hash of chain>
     */
    @Throws(
        SoramitsuNetworkException::class,
        ChainBuilderException::class,
        CancellationException::class
    )
    suspend fun getChains(
        version: String,
        existedChains: List<Pair<String, String>>
    ): ResultChainInfo {
        val indexResponse = networkClient.createJsonRequest<JsonElement>(baseUrl + indexFile)
        val curVersion = version.versionSplit()
        val chainList = runCatching {
            val keysList = (indexResponse as JsonObject).keys.toList()
            val selectedVersion = keysList.first { v ->
                v.versionSplit().match(curVersion)
            }
            Json.decodeFromJsonElement(
                ListSerializer(ChainResponse.serializer()),
                indexResponse[selectedVersion]!!
            )
        }.getOrElse {
            throw VersionNotFoundException(
                "Version [${curVersion.joinToString(".")}] not found in [$indexFile]",
                null
            )
        }
        val removedChains = existedChains.map { it.first }.minus(chainList.map { it.id }.toSet())
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
