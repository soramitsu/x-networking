package jp.co.soramitsu.xnetworking.basic.tbd

import kotlinx.cinterop.addressOf
import kotlinx.cinterop.alloc
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.ptr
import kotlinx.cinterop.refTo
import kotlinx.cinterop.usePinned
import platform.CoreCrypto.CC_SHA256_CTX
import platform.CoreCrypto.CC_SHA256_Final
import platform.CoreCrypto.CC_SHA256_Init
import platform.CoreCrypto.CC_SHA256_Update

actual fun sha(a: ByteArray): String = ""
//actual fun sha(a: ByteArray): String = memScoped {
//    val ctx = alloc<CC_SHA256_CTX>()
//    CC_SHA256_Init(ctx.ptr)
//    //CC_SHA256_Update(ctx.ptr, a.refTo(0), a.size)
//    val s: String
//    a.asUByteArray().usePinned {
//        CC_SHA256_Final(it.addressOf(0), ctx.ptr)
//    }
//    ""
//}