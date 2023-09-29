package jp.co.soramitsu.xnetworking.sorawallet.core.datasources.polkaswapconfig.impl

import jp.co.soramitsu.xnetworking.sorawallet.core.datasources.polkaswapconfig.api.ConfigRepository
import jp.co.soramitsu.xnetworking.sorawallet.core.datasources.polkaswapconfig.api.models.common.CommonConfig
import jp.co.soramitsu.xnetworking.sorawallet.core.datasources.polkaswapconfig.api.models.mobile.MobileConfig
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class ConfigRepositoryCachingDecorator(
    private val configRepository: ConfigRepository
): ConfigRepository {

    private val commonConfigMutex: Mutex = Mutex()

    private var commonConfig: CommonConfig? = null
        get() {
            return runBlocking {
                commonConfigMutex.withLock {
                    field
                }
            }
        }
        set(value) {
            runBlocking {
                commonConfigMutex.withLock {
                    field = value
                }
            }
        }

    private val mobileConfigMutex: Mutex = Mutex()

    private var mobileConfig: MobileConfig? = null
        get() {
            return runBlocking {
                mobileConfigMutex.withLock {
                    field
                }
            }
        }
        set(value) {
            runBlocking {
                mobileConfigMutex.withLock {
                    field = value
                }
            }
        }

    override suspend fun getCommonConfig() = commonConfig
        ?: configRepository.getCommonConfig().also { commonConfig = it }

    override suspend fun getMobileConfig() = mobileConfig
        ?: configRepository.getMobileConfig().also { mobileConfig = it }
}