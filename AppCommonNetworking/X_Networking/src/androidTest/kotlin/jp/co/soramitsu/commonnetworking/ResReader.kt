package jp.co.soramitsu.commonnetworking

actual fun readBinaryResource(
    resourceName: String
): ByteArray {
    return ClassLoader
        .getSystemResourceAsStream(resourceName)
        .readBytes()
}