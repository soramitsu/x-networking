package jp.co.soramitsu.xnetworking.sorawallet.core.datasources.blockexplorer.impl.rest.fiat

data class FiatData(
    val id: String,
    val priceUsd: Double? = null,
)
