package jp.co.soramitsu.xnetworking.lib.engines.utils

import kotlinx.serialization.Serializable
import kotlin.experimental.ExperimentalObjCRefinement
import kotlin.native.HiddenFromObjC

/*
    GraphQLResponseDataWrapper can be used in main part;
    but for iOS it is hidden, since iOS can't handle generics; thus,
    iOS should always request for NSString result, and deserialize it on its own
*/
@Serializable
@OptIn(ExperimentalObjCRefinement::class)
@HiddenFromObjC
data class GraphQLResponseDataWrapper<T>(
    val data: T
)

@Serializable
class GraphQLSerializableRequestWrapper(
    val query: String
) {
    override fun equals(other: Any?): Boolean {
        if (other !is GraphQLSerializableRequestWrapper)
            return false

        return query == other.query
    }

    override fun hashCode(): Int {
        return query.hashCode()
    }
}
