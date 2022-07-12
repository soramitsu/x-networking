package jp.co.soramitsu.commonnetworking.fearless

import kotlinx.serialization.Serializable

@Serializable
data class ChainModel(
    val chainId: String,
    val hash: String,
    val content: String,
)

data class ResultChainInfo(
    val newChains: List<ChainModel>,
    val updatedChains: List<ChainModel>,
    val removedChains: List<String>,
)

@Serializable
data class ChainResponse(
    val chain: String,
    val hash: String,
    val id: String,
)
