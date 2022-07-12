package jp.co.soramitsu.commonnetworking.fearless

internal actual fun platform(): String = "android"

// content this.PackageManager.GetPackageInfo(this.PackageName, 0)
internal actual fun version(): String = ""
