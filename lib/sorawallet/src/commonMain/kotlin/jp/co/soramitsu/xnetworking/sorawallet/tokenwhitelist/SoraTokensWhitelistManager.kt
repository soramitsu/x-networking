package jp.co.soramitsu.xnetworking.sorawallet.tokenwhitelist

import io.ktor.http.*
import io.ktor.util.*
import jp.co.soramitsu.xnetworking.basic.networkclient.SoramitsuNetworkClient
import jp.co.soramitsu.xnetworking.basic.networkclient.SoramitsuNetworkException
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlin.coroutines.cancellation.CancellationException

/**
 * @param rawIcon bytearray if png, string if svg
 * @param type type of icon. svg or png
 */
data class SoraTokenWhitelistDto(
    val address: String,
    val rawIcon: Any,
    val type: String,
)

class SoraTokensWhitelistManager(
    private val networkClient: SoramitsuNetworkClient,
) {

    companion object {
        private const val url = "https://whitelist.polkaswap2.io/whitelist.json"
    }

    @Throws(SoramitsuNetworkException::class, CancellationException::class)
    suspend fun getTokens(): List<SoraTokenWhitelistDto> {
        val response = networkClient.createJsonRequest<JsonArray>(url)
        return response.mapNotNull {
            val json = it as JsonObject
            val address = json["address"]?.jsonPrimitive?.content.orEmpty()
            val rawIconField = json["icon"]?.jsonPrimitive?.content.orEmpty()
            val rawIcon = rawIconField.substringAfter(",", "")
            when {
                rawIconField.startsWith("data:image/svg") -> {
                    SoraTokenWhitelistDto(
                        address = address,
                        rawIcon = rawIcon.decodeURLPart(),
                        type = "svg",
                    )
                }
                rawIconField.startsWith("data:image/png") -> {
                    SoraTokenWhitelistDto(
                        address = address,
                        rawIcon = rawIcon.decodeBase64Bytes(),
                        type = "png",
                    )
                }
                else -> null
            }
        }
    }
}
