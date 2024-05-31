package jp.co.soramitsu.xnetworking.lib.engines.utils

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO

actual val Dispatchers.CommonIO: CoroutineDispatcher
    get() = IO