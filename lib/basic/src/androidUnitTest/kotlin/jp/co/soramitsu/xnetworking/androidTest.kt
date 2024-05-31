package jp.co.soramitsu.xnetworking

import jp.co.soramitsu.xnetworking.basic.common.platform
import org.junit.Assert.assertEquals
import org.junit.Test

class AndroidGreetingTest {

    @Test
    fun testExample() {
        assertEquals("android", platform())
    }
}