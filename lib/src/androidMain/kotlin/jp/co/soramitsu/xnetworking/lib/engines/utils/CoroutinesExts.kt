package jp.co.soramitsu.xnetworking.lib.engines.utils

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

actual val Dispatchers.CommonIO: CoroutineDispatcher
    get() = Dispatchers.IO