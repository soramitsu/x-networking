package jp.co.soramitsu.xnetworking.lib.datasources.chainsconfig.api.models

abstract class ExternalApiType {

    internal abstract val nameAsInConfig: String

    data object EtherScan: ExternalApiType() {
        override val nameAsInConfig: String = "etherscan"
    }

    data object GiantSquid: ExternalApiType() {
        override val nameAsInConfig: String = "giantsquid"
    }

    data object GitHub: ExternalApiType() {
        override val nameAsInConfig: String = "github"
    }

    data object OkLink: ExternalApiType() {
        override val nameAsInConfig: String = "oklink"
    }

    data object Reef: ExternalApiType() {
        override val nameAsInConfig: String = "reef"
    }

    data object Sora: ExternalApiType() {
        override val nameAsInConfig: String = "sora"
    }

    data object SubQuery: ExternalApiType() {
        override val nameAsInConfig: String = "subquery"
    }

    data object SubSquid: ExternalApiType() {
        override val nameAsInConfig: String = "subsquid"
    }

    data object Unknown: ExternalApiType() {
        override val nameAsInConfig: String = "unknown"
    }

    data object Zeta: ExternalApiType() {
        override val nameAsInConfig: String = "zeta"
    }

    companion object {
        private val mapping by lazy {
            mapOf(
                EtherScan.nameAsInConfig to EtherScan,
                GiantSquid.nameAsInConfig to GiantSquid,
                GitHub.nameAsInConfig to GitHub,
                OkLink.nameAsInConfig to OkLink,
                Reef.nameAsInConfig to Reef,
                Sora.nameAsInConfig to Sora,
                SubQuery.nameAsInConfig to SubQuery,
                SubSquid.nameAsInConfig to SubSquid,
                Unknown.nameAsInConfig to Unknown,
                Zeta.nameAsInConfig to Zeta
            )
        }

        fun valueOf(value: String?): ExternalApiType? = mapping[value]
    }
}