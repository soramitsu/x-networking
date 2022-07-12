package jp.co.soramitsu.commonnetworking

import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.fullPath
import io.ktor.http.headersOf
import io.ktor.serialization.kotlinx.json.json
import jp.co.soramitsu.commonnetworking.fearless.FearlessChainsBuilder
import jp.co.soramitsu.commonnetworking.fearless.VersionNotFoundException
import jp.co.soramitsu.commonnetworking.networkclient.SoramitsuHttpClientProvider
import jp.co.soramitsu.commonnetworking.networkclient.SoramitsuNetworkClient
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class FearlessChainsBuilderTest {

    lateinit var client: SoramitsuNetworkClient
    lateinit var builder: FearlessChainsBuilder

    private val response = readBinaryResource("index.json").decodeToString()
    private val mockEngine = MockEngine { request ->
        respond(
            content = if (request.url.fullPath.contains("index")) response else "some_content_${request.url.encodedPath}",
            status = HttpStatusCode.OK,
            headers = headersOf(HttpHeaders.ContentType, "application/json")
        )
    }

    @BeforeTest
    fun setUp() {
        client = SoramitsuNetworkClient(
            provider = object : SoramitsuHttpClientProvider {
                override fun provide(logging: Boolean, timeout: Long, json: Json): HttpClient {
                    return HttpClient(mockEngine) {
                        install(ContentNegotiation) {
                            json(
                                Json {
                                    prettyPrint = true
                                    isLenient = true
                                    ignoreUnknownKeys = true
                                },
                                contentType = ContentType.Any
                            )
                        }
                    }
                }
            }
        )
        builder = FearlessChainsBuilder(client, "https://www.sora.org/")
    }

    @Test
    fun test_version_above() = runTest {
        val res = builder.getChains("2.4.1", emptyList())
        assertEquals(3, res.newChains.size)
    }

    @Test
    fun test_version_below() = runTest {
        val res = builder.getChains("2.0.3", emptyList())
        assertEquals(2, res.newChains.size)
    }

    @Test
    fun test_version_below2() = runTest {
        val res = builder.getChains("2.0.0", emptyList())
        assertEquals(2, res.newChains.size)
    }

    @Test
    fun test_version_below3() = runTest {
        runCatching { builder.getChains("1.0.0", emptyList()) }
            .onFailure {
                assertTrue(it is VersionNotFoundException)
            }
    }

    @Test
    fun test_1_updated_1_new() = runTest {
        val res = builder.getChains("2.4.1", listOf("123" to "qwe", "456" to "another"))
        assertEquals(1, res.newChains.size)
        assertEquals("789", res.newChains.first().chainId)
        assertEquals(1, res.updatedChains.size)
        assertEquals("456", res.updatedChains.first().chainId)
    }

    @Test
    fun test_1_updated_1_new_1_removed() = runTest {
        val res =
            builder.getChains("2.4.1", listOf("123" to "qwe", "456" to "another", "rem" to "remh"))
        assertEquals(1, res.newChains.size)
        assertEquals("789", res.newChains.first().chainId)
        assertEquals(1, res.updatedChains.size)
        assertEquals("456", res.updatedChains.first().chainId)
        assertEquals(1, res.removedChains.size)
        assertEquals("rem", res.removedChains.first())
    }
}