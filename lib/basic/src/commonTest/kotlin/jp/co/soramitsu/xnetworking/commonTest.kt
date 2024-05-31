package jp.co.soramitsu.xnetworking

import jp.co.soramitsu.xnetworking.basic.common.platform
import kotlin.test.Test
import kotlin.test.assertTrue

class CommonGreetingTest {

    @Test
    fun testExample() {
        assertTrue(platform().contains("o"))
    }
}