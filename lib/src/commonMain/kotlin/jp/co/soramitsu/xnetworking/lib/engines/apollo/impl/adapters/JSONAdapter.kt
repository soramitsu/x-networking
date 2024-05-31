package jp.co.soramitsu.xnetworking.lib.engines.apollo.impl.adapters

import com.apollographql.apollo3.api.Adapter
import com.apollographql.apollo3.api.AnyAdapter
import com.apollographql.apollo3.api.CustomScalarAdapters
import com.apollographql.apollo3.api.json.JsonNumber
import com.apollographql.apollo3.api.json.JsonReader
import com.apollographql.apollo3.api.json.JsonWriter
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive

class JSONAdapter: Adapter<JsonElement> {
    override fun fromJson(
        reader: JsonReader,
        customScalarAdapters: CustomScalarAdapters
    ) = adaptDeserialization(
        any = AnyAdapter.fromJson(
            reader = reader,
            customScalarAdapters = customScalarAdapters
        )
    )

    private fun adaptDeserialization(any: Any): JsonElement =
        when(any) {
            is Boolean -> JsonPrimitive(any)
            is Int -> JsonPrimitive(any)
            is Long -> JsonPrimitive(any)
            is Double -> JsonPrimitive(any)
            is JsonNumber -> JsonPrimitive(any.value)
            is String -> JsonPrimitive(any)
            is Map<*, Any?> -> any.mapKeys {
                it.key.toString()
            }.mapValues {
                it.value?.let { adaptDeserialization(it) } ?: JsonNull
            }.run { JsonObject(this) }
            is List<Any?> -> any.filterNotNull().map {
                adaptDeserialization(it)
            }.run { JsonArray(this) }
            else -> error("unknown any instance $any")
        }

    override fun toJson(
        writer: JsonWriter,
        customScalarAdapters: CustomScalarAdapters,
        value: JsonElement
    ) = AnyAdapter.toJson(
        writer = writer,
        customScalarAdapters = customScalarAdapters,
        value = adaptSerialization(value)
    )

    private fun adaptSerialization(element: JsonElement): Any =
        when(element) {
            is JsonArray -> element.map {
                adaptSerialization(it)
            }
            is JsonObject -> element.mapValues {
                adaptSerialization(it.value)
            }
            is JsonPrimitive ->
                when {
                    element.content.toBooleanStrictOrNull() != null ->
                        element.content.toBooleanStrict()
                    element.content.toIntOrNull() != null ->
                        element.content.toInt()
                    element.content.toLongOrNull() != null ->
                        element.content.toLong()
                    element.content.toDoubleOrNull() != null ->
                        element.content.toDouble()
                    element.isString ->
                        element.content
                    else -> error("Unknown primitive type")
                }
            else -> error("Unknown element instance $element")
        }

}