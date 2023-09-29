package jp.co.soramitsu.xnetworking.sorawallet.core.datasources.blockexplorer.impl.rest.sbapy

data class SbApyInfo(
    val id: String,
    val priceUsd: Double? = null,
    val sbApy: Double? = null,
)
