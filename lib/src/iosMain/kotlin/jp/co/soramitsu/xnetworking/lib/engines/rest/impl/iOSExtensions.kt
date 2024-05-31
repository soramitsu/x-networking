package jp.co.soramitsu.xnetworking.lib.engines.rest.impl

import jp.co.soramitsu.xnetworking.lib.engines.rest.api.RestClient
import jp.co.soramitsu.xnetworking.lib.engines.rest.api.models.RestClientException
import jp.co.soramitsu.xnetworking.lib.engines.utils.JsonGetRequestIOS
import jp.co.soramitsu.xnetworking.lib.engines.utils.JsonPostRequestIOS
import kotlin.coroutines.cancellation.CancellationException
import kotlin.experimental.ExperimentalObjCName

/*
    While converting code to ObjC and then Swift, generic information is lost;
    in addition to this, Serialization from iOS client side with kotlinx.serialization
    is not possible due to inability to use KSP; thus,
    the best option is to load strings, and let iOS client side (de)serialize
    everything that is needed in most appropriate way
 */
@OptIn(ExperimentalObjCName::class)
@ObjCName("get")
@Throws(
    RestClientException::class,
    CancellationException::class
)
suspend fun RestClient.getIOS(
    request: JsonGetRequestIOS
): String = get(request)

/*
    While converting code to ObjC and then Swift, generic information is lost;
    in addition to this, Serialization from iOS client side with kotlinx.serialization
    is not possible due to inability to use KSP; thus,
    the best option is to load strings, and let iOS client side (de)serialize
    everything that is needed in most appropriate way
 */
@OptIn(ExperimentalObjCName::class)
@ObjCName("post")
@Throws(
    RestClientException::class,
    CancellationException::class
)
suspend fun RestClient.postIOS(
    request: JsonPostRequestIOS
): String = get(request)