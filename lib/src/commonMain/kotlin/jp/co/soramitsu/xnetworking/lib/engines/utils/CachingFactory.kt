package jp.co.soramitsu.xnetworking.lib.engines.utils

import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

internal class CachingFactory<Args: CachingFactory.Args, Value>(
    private val factory: Args.() -> Value
) {

    abstract class Args

    private val cacheMutex = Mutex()
    private var cachedValue = mutableMapOf<Args, Value>()

    suspend fun getOrCreate(args: Args): Value {
        if (cachedValue[args] == null) {
            cacheMutex.withLock {
                if (cachedValue[args] == null) {
                    cachedValue[args] = factory(args)
                }
            }
        }

        return checkNotNull(cachedValue[args])
    }

}