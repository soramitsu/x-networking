package jp.co.soramitsu.xnetworking.lib.datasources.txhistory.impl.domain.utils

import jp.co.soramitsu.xnetworking.lib.datasources.txhistory.api.utils.PackedCursor
import jp.co.soramitsu.xnetworking.lib.datasources.txhistory.impl.utils.PackedCursorImpl
import kotlin.experimental.ExperimentalObjCName


/*
    PackedCursor.create() is an extension that creates property reader for better
    visibility cues; but on iOS client side this feature is unavailable; thus,
    this is a helper function that allows to create PackedCursor on iOS
    in similar semantics to Kotlin's
 */
@OptIn(ExperimentalObjCName::class)
@ObjCName("create")
fun PackedCursor.Companion.createIOS(cursor: String?): PackedCursor = PackedCursorImpl(cursor)