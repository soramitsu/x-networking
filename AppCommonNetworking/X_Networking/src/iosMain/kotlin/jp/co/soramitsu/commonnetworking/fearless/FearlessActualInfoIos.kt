package jp.co.soramitsu.commonnetworking.fearless

import platform.Foundation.NSBundle

internal actual fun platform(): String = "ios"

//CFBundleShortVersionString
internal actual fun version(): String = NSBundle.mainBundle.objectForInfoDictionaryKey("CFBundleVersion").toString()