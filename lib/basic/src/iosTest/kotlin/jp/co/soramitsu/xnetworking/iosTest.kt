package jp.co.soramitsu.xnetworking

import jp.co.soramitsu.xnetworking.basic.common.platform
import kotlin.test.Test
import kotlin.test.assertEquals

class IosGreetingTest {

    @Test
    fun testExample() {
        assertEquals("ios", platform())
    }
}