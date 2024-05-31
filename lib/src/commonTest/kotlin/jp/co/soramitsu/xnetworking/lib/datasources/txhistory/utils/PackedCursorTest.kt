package jp.co.soramitsu.xnetworking.lib.datasources.txhistory.utils

import jp.co.soramitsu.xnetworking.lib.datasources.txhistory.api.utils.PackedCursor
import jp.co.soramitsu.xnetworking.lib.datasources.txhistory.impl.utils.PackedCursorImpl
import jp.co.soramitsu.xnetworking.lib.datasources.txhistory.impl.utils.create
import kotlin.test.Test
import kotlin.test.assertTrue

class PackedCursorTest {

    @Test
    fun `TEST packedCursor_pack EXPECT success WHEN cursor is blank`() {
        // Test Data Start
        val cursor = ""
        val expectedResult = null
        // Test Data Start

        val packedCursor by PackedCursor.create(cursor)

        // Verification & Assertion
        assertTrue { packedCursor.pack() == expectedResult }
    }

    @Test
    fun `TEST packedCursor_pack EXPECT success WHEN cursor has corrupted data LIKE keyValueDelimiter`() {
        // Test Data Start
        val cursor = PackedCursorImpl.KEY_VALUE_DELIMITER
        val expectedResult = null
        // Test Data Start

        val packedCursor by PackedCursor.create(cursor)

        // Verification & Assertion
        assertTrue { packedCursor.pack() == expectedResult }
    }

    @Test
    fun `TEST packedCursor_pack EXPECT success WHEN cursor has corrupted data LIKE pairDelimiter`() {
        // Test Data Start
        val cursor = PackedCursorImpl.PAIR_DELIMITER
        val expectedResult = null
        // Test Data Start

        val packedCursor by PackedCursor.create(cursor)

        // Verification & Assertion
        assertTrue { packedCursor.pack() == expectedResult }
    }

    @Test
    fun `TEST packedCursor_pack EXPECT success WHEN cursor has corrupted data AND put is performed`() {
        // Test Data Start
        val cursor = PackedCursorImpl.PAIR_DELIMITER
        val expectedResult = "test:null"
        // Test Data Start

        val packedCursor by PackedCursor.create(cursor)

        packedCursor["test"] = null

        // Verification & Assertion
        assertTrue { packedCursor.pack() == expectedResult }
    }

    @Test
    fun `TEST packedCursor_pack EXPECT success WHEN cursor has corrupted data AND put of additional delimiter is performed`() {
        // Test Data Start
        val cursor = PackedCursorImpl.PAIR_DELIMITER
        val expectedResult = null
        // Test Data Start

        val packedCursor by PackedCursor.create(cursor)

        packedCursor["test"] = PackedCursorImpl.PAIR_DELIMITER

        // Verification & Assertion
        assertTrue { packedCursor.pack() == expectedResult }
    }

    @Test
    fun `TEST packedCursor_get EXPECT success WHEN cursor has corrupted data`() {
        // Test Data Start
        val cursor = PackedCursorImpl.PAIR_DELIMITER
        val expectedResult = null
        // Test Data Start

        val packedCursor by PackedCursor.create(cursor)

        // Verification & Assertion
        assertTrue { packedCursor["test"] == expectedResult }
    }

    @Test
    fun `TEST packedCursor_get EXPECT success WHEN cursor has inconsistencies`() {
        // Test Data Start
        val cursor = "test:${PackedCursorImpl.PAIR_DELIMITER}"
        val expectedResult = null
        // Test Data Start

        val packedCursor by PackedCursor.create(cursor)

        // Verification & Assertion
        assertTrue { packedCursor["test"] == expectedResult }
    }

    @Test
    fun `TEST packedCursor_get EXPECT success WHEN cursor has additional delimiter`() {
        // Test Data Start
        val cursor = "test:1${PackedCursorImpl.PAIR_DELIMITER}"
        val expectedResult = "1"
        // Test Data Start

        val packedCursor by PackedCursor.create(cursor)

        // Verification & Assertion
        assertTrue { packedCursor["test"] == expectedResult }
    }

    @Test
    fun `TEST packedCursor_set EXPECT success WHEN cursor already has key`() {
        // Test Data Start
        val cursor = "test:1${PackedCursorImpl.PAIR_DELIMITER}"
        val expectedResult = null
        // Test Data Start

        val packedCursor by PackedCursor.create(cursor)

        packedCursor["test"] = null

        // Verification & Assertion
        assertTrue { packedCursor["test"] == expectedResult }
    }

}