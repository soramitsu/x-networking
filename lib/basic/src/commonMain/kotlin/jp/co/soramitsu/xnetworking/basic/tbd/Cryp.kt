package jp.co.soramitsu.xnetworking.basic.tbd

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

expect fun sha(a: ByteArray): String

class Cryp {

    fun doFlow(): Flow<Int> = flow {
        emit(1)
        delay(700)
        emit(2)
        delay(700)
        emit(3)
        delay(700)
        emit(4)
        delay(700)
    }
}