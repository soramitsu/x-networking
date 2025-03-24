//package jp.co.soramitsu.xnetworking.lib.engines.utils
//
//import io.mockative.coEvery
//import io.mockative.coVerify
//import io.mockative.mock
//import io.mockative.of
//import jp.co.soramitsu.xnetworking.lib.engines.rest.api.RestClient
//import kotlinx.coroutines.test.runTest
//import kotlinx.serialization.builtins.serializer
//import kotlin.test.Test
//
//class RestClientExts {
//
//    private val restClientMock = mock(of<RestClient>())
//
//    @Test
//    fun `TEST restClient_getAsString_String EXPECT success`() = runTest {
//        val url = "myUrl"
//        val result = "myResult"
//
//        coEvery {
//            restClientMock.getAsString(
//                request = JsonGetRequest(
//                    url = url,
//                    responseDeserializer = String.serializer()
//                )
//            )
//        }.returns(result)
//
//        restClientMock.getAsString(
//            url = url
//        )
//
//        coVerify {
//            restClientMock.getAsString(
//                request = JsonGetRequest(
//                    url = url,
//                    responseDeserializer = String.serializer()
//                )
//            )
//        }.wasInvoked(1)
//    }
//
//    @Test
//    fun `TEST restClient_postAsString_String EXPECT success`() = runTest {
//        val url = "myUrl"
//        val body = "myBody"
//        val result = "myResult"
//
//        coEvery {
//            restClientMock.postAsString(
//                request = JsonPostRequest(
//                    url = url,
//                    body = body,
//                    responseDeserializer = String.serializer()
//                )
//            )
//        }.returns(result)
//
//        restClientMock.postAsString(
//            url = url,
//            body = body
//        )
//
//        coVerify {
//            restClientMock.postAsString(
//                request = JsonPostRequest(
//                    url = url,
//                    body = body,
//                    responseDeserializer = String.serializer()
//                )
//            )
//        }.wasInvoked(1)
//    }
//
//}