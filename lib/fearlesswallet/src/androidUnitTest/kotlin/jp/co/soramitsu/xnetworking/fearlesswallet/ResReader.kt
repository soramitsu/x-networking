package jp.co.soramitsu.xnetworking.fearlesswallet

actual fun readBinaryResource(
    resourceName: String
): ByteArray {
    return ClassLoader
        .getSystemResourceAsStream(resourceName)
        .readBytes()
}