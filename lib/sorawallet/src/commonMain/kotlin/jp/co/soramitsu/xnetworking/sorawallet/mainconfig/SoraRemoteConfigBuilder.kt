package jp.co.soramitsu.xnetworking.sorawallet.mainconfig

import com.russhwolf.settings.ExperimentalSettingsApi
import com.russhwolf.settings.Settings
import com.russhwolf.settings.serialization.decodeValueOrNull
import com.russhwolf.settings.serialization.encodeValue
import jp.co.soramitsu.xnetworking.basic.common.platform
import jp.co.soramitsu.xnetworking.basic.common.platform_android
import jp.co.soramitsu.xnetworking.basic.networkclient.SoramitsuNetworkClient
import jp.co.soramitsu.xnetworking.basic.networkclient.SoramitsuNetworkException
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.coroutines.cancellation.CancellationException

class SoraRemoteConfigBuilder(
    private val client: SoramitsuNetworkClient,
    private val commonUrl: String,
    private val mobileUrl: String,
    private val settings: Settings,
) {
    private var m = Mutex()
    private var config: SoraConfig? = null

    private companion object {
        private const val commonConfigName = "commonConfig"
        private const val mobileConfigName = "mobileConfig"
    }

    @Throws(SoramitsuNetworkException::class, CancellationException::class)
    suspend fun getConfig(): SoraConfig? = config ?: m.withLock {
        config ?: buildConfig().also {
            config = it
        }
    }

    @OptIn(ExperimentalSerializationApi::class, ExperimentalSettingsApi::class)
    @Throws(CancellationException::class)
    private suspend fun buildConfig(): SoraConfig? {
        var configDto: ConfigDto? = null
        var mobileDto: MobileDto? = null
        var remote = true
        try {
            configDto = client.createJsonRequest(commonUrl)
            mobileDto = client.createJsonRequest(mobileUrl)
            settings.encodeValue(ConfigDto.serializer(), commonConfigName, configDto)
            settings.encodeValue(MobileDto.serializer(), mobileConfigName, mobileDto)
        } catch (t: Throwable) {
            if (t is SoramitsuNetworkException) {
                configDto = settings.decodeValueOrNull(ConfigDto.serializer(), commonConfigName)
                mobileDto = settings.decodeValueOrNull(MobileDto.serializer(), mobileConfigName)
                remote = false
            }
        }
        return if (configDto == null || mobileDto == null) {
            null
        } else {
            SoraConfig(
                remote = remote,
                blockExplorerUrl = configDto.subquery,
                blockExplorerType = ConfigExplorerType(
                    fiat = mobileDto.explorerTypeFiat,
                    reward = mobileDto.explorerTypeReward,
                    sbapy = mobileDto.explorerTypeSbapy,
                    assets = mobileDto.explorerTypeAssets,
                ),
                nodes = configDto.nodes.map {
                    SoraConfigNode(
                        chain = it.chain,
                        name = it.name,
                        address = it.address,
                    )
                },
                genesis = configDto.genesis,
                joinUrl = mobileDto.joinLink,
                substrateTypesUrl = if (platform() == platform_android) mobileDto.substrateTypesAndroid else mobileDto.substrateTypesIos,
                soracard = mobileDto.soracard,
                currencies = mobileDto.currencies.map {
                    SoraCurrency(
                        code = it.code,
                        name = it.name,
                        sign = it.sign,
                    )
                }
            )
        }
    }
}

@Serializable
private data class ConfigDto(
    @SerialName("SUBQUERY_ENDPOINT")
    val subquery: String,
    @SerialName("DEFAULT_NETWORKS")
    val nodes: List<NodeInfo>,
    @SerialName("CHAIN_GENESIS_HASH")
    val genesis: String,
)

@Serializable
private data class NodeInfo(
    @SerialName("chain")
    val chain: String,
    @SerialName("name")
    val name: String,
    @SerialName("address")
    val address: String,
)

@Serializable
private data class MobileDto(
    @SerialName("explorer_type_fiat")
    val explorerTypeFiat: String,
    @SerialName("explorer_type_sbapy")
    val explorerTypeSbapy: String,
    @SerialName("explorer_type_reward")
    val explorerTypeReward: String,
    @SerialName("explorer_type_assets")
    val explorerTypeAssets: String,
    @SerialName("join_link")
    val joinLink: String,
    @SerialName("substrate_types_android")
    val substrateTypesAndroid: String,
    @SerialName("substrate_types_ios")
    val substrateTypesIos: String,
    @SerialName("soracard")
    val soracard: Boolean = false,
    @SerialName("currencies")
    val currencies: List<CurrencyDto>,
)

@Serializable
private data class CurrencyDto(
    @SerialName("code")
    val code: String,
    @SerialName("name")
    val name: String,
    @SerialName("sign")
    val sign: String,
)
