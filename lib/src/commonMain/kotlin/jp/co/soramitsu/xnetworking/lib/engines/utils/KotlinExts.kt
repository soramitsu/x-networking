package jp.co.soramitsu.xnetworking.lib.engines.utils

inline fun <reified T : Enum<T>> enumValueOfNullable(type: String?): T? {
    return try {
        type?.let { enumValueOf<T>(it.uppercase()) }
    } catch (e: IllegalArgumentException) {
        null
    }
}