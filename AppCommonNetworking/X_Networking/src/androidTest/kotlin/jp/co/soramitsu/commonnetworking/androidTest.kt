package jp.co.soramitsu.commonnetworking

import jp.co.soramitsu.commonnetworking.tbd.sha
import org.junit.Assert.assertEquals
import org.junit.Test

class AndroidGreetingTest {

    @Test
    fun testExample() {
        assertEquals("", sha(byteArrayOf()))
    }
}