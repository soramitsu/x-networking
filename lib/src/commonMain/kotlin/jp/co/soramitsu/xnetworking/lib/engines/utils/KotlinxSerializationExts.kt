package jp.co.soramitsu.xnetworking.lib.engines.utils

import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive

val JsonElement?.asJsonArrayNullable: JsonArray?
    get() = this as? JsonArray

val JsonElement?.asJsonObjectNullable: JsonObject?
    get() = this as? JsonObject

inline fun JsonElement?.arrayOrNull(key: String) =
    (this as? JsonObject)?.get(key)?.let { it as? JsonArray }

inline fun JsonElement?.objectOrNull(key: String) =
    (this as? JsonObject)?.get(key)?.let { it as? JsonObject }

inline fun JsonElement?.fieldOrNull(key: String) =
    (this as? JsonObject)?.get(key).primitiveOrNull()

inline fun JsonElement?.primitiveOrNull() =
    (this as? JsonPrimitive)?.content
