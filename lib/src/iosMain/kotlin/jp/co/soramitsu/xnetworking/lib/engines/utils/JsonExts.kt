package jp.co.soramitsu.xnetworking.lib.engines.utils

import kotlinx.serialization.json.Json

fun createJson(
    isPrettyPrintEnabled: Boolean,
    isLenient: Boolean,
    shouldIgnoreUnknownKeys: Boolean
): Json {
    return Json {
        this.prettyPrint = isPrettyPrintEnabled
        this.isLenient = isLenient
        this.ignoreUnknownKeys = shouldIgnoreUnknownKeys
    }
}