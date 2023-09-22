package jp.co.soramitsu.xnetworking.fearlesswallet.common.interactors.txhistory.impl

import jp.co.soramitsu.xnetworking.basic.engines.apollo.api.ApolloClientStore

internal val ApolloClientStore.Companion.FEARLESS_SUBQUERY_TAG: Int
    get() = 1
