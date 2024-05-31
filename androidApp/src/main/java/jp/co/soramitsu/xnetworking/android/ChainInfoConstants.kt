package jp.co.soramitsu.xnetworking.android

import jp.co.soramitsu.xnetworking.lib.datasources.txhistory.api.models.ChainInfo

enum class ChainInfoConstants(val chainInfo: ChainInfo) {

    EtherScan(
        chainInfo = ChainInfo.WithEthereumType("1", "", ""),
    ),
    GiantSquid(
        chainInfo = ChainInfo.Simple("48239ef607d7928874027a43a67689209727dfb3d3dc5e5b03a39bdc2eda771a")
    ),
    OkLink(
        chainInfo = ChainInfo.WithAssetSymbol("195", "okb")
    ),
    Reef(
        chainInfo = ChainInfo.Simple("7834781d38e4798d548e34ec947d19deea29df148a7bf32484b7b24dacf8d4b7")
    ),
    Sora(
        chainInfo = ChainInfo.Simple("3266816be9fa51b32cfea58d3e33ca77246bc9618595a4300e44c8856a8d8a17")
    ),
    SubSquid(
        chainInfo = ChainInfo.Simple("91b171bb158e2d3848fa23a9f1c25182fb8e20313b2c1eb49219da7a70ce90c3")
    ),
    SubQuery(
        chainInfo = ChainInfo.Simple("cd4d732201ebe5d6b014edda071c4203e16867305332301dc8d092044b28e554")
    ),
    Zeta(
        chainInfo = ChainInfo.WithEthereumType("7001", "", "")
    )

}