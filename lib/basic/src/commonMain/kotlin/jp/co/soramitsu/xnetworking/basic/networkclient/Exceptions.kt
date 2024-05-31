package jp.co.soramitsu.xnetworking.basic.networkclient

class SoramitsuNetworkException(val m: String, c: Throwable?, val reason: String) : Throwable(m, c)
