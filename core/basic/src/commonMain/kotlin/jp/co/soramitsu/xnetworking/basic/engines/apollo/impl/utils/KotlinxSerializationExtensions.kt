package jp.co.soramitsu.xnetworking.basic.engines.apollo.impl.utils

import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive

val JsonElement.asJsonArrayNullable: JsonArray?
    get() = this as? JsonArray

val JsonElement.asJsonObjectNullable: JsonObject?
    get() = this as? JsonObject

inline fun JsonObject?.getPrimitiveContentOrEmpty(key: String) =
    this?.get(key)?.asJsonPrimitiveNullable?.content.orEmpty()

val JsonElement.asJsonPrimitiveNullable: JsonPrimitive?
    get() = this as? JsonPrimitive

inline fun JsonPrimitive?.getContentOrEmpty() =
    this?.content.orEmpty()