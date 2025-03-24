package jp.co.soramitsu.xnetworking.lib.datasources.txhistory.impl.utils

import jp.co.soramitsu.xnetworking.lib.datasources.txhistory.api.utils.PackedCursor
import kotlin.experimental.ExperimentalObjCRefinement
import kotlin.native.HiddenFromObjC
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

@OptIn(ExperimentalObjCRefinement::class)
@HiddenFromObjC
fun PackedCursor.Companion.create(cursor: String?): ReadOnlyProperty<Any?, PackedCursor> =
    object : ReadOnlyProperty<Any?, PackedCursor> {
        private val packedCursor = PackedCursorImpl(cursor = cursor)

        override fun getValue(thisRef: Any?, property: KProperty<*>): PackedCursor = packedCursor
    }

internal class PackedCursorImpl(cursor: String?): PackedCursor() {
    internal companion object {
        const val KEY_VALUE_DELIMITER = ":"
        const val PAIR_DELIMITER = ";"
    }

    private val cursorByKeyMap: MutableMap<String, String?> = cursor.orEmpty()
        .split(PAIR_DELIMITER)
        .associate {
            val key = it.substringBefore(
                delimiter = KEY_VALUE_DELIMITER,
                missingDelimiterValue = ""
            )
            val cursorValue = it.substringAfter(
                delimiter = KEY_VALUE_DELIMITER,
                missingDelimiterValue = ""
            )

            key to cursorValue
        }.filter { (key, cursorValue) ->
            // if cursorValue isBlank get on it should return predictable null; thus we skip it here
            // and return null in get method
            key.isNotBlank() && cursorValue.isNotBlank()
        }.toMutableMap()

    override fun set(key: String, cursor: String?) {
        if (cursor != KEY_VALUE_DELIMITER && cursor != PAIR_DELIMITER)
            cursorByKeyMap[key] = cursor
    }

    override fun get(key: String) = cursorByKeyMap[key]

    override fun pack(): String? = with(StringBuilder()) {
        cursorByKeyMap.onEachIndexed { index, (key, cursor) ->
            append(key)
            append(KEY_VALUE_DELIMITER)
            append(cursor)

            if (index != cursorByKeyMap.size - 1) {
                append(PAIR_DELIMITER)
            }
        }

        if (this.isBlank())
            return@with null

        return@with this.toString()
    }
}