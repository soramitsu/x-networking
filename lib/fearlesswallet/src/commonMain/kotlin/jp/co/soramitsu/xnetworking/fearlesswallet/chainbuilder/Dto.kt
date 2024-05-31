package jp.co.soramitsu.xnetworking.fearlesswallet.chainbuilder

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ChainModel(
    @SerialName("chainId")
    val chainId: String,
    @SerialName("hash")
    val hash: String,
    @SerialName("content")
    val content: String,
)

data class ResultChainInfo(
    val newChains: List<ChainModel>,
    val updatedChains: List<ChainModel>,
    val removedChains: List<String>,
)

@Serializable
data class ChainResponse(
    @SerialName("chain")
    val chain: String,
    @SerialName("hash")
    val hash: String,
    @SerialName("id")
    val id: String,
)
