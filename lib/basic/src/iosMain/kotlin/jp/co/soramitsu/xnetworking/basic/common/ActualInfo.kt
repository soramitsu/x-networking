package jp.co.soramitsu.xnetworking.basic.common

actual fun platform(): String = platform_ios

//CFBundleShortVersionString
//internal actual fun version(): String = NSBundle.mainBundle.objectForInfoDictionaryKey("CFBundleVersion").toString()