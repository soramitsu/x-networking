package jp.co.soramitsu.xnetworking.android

import jp.co.soramitsu.xnetworking.lib.datasources.txhistory.api.models.ChainInfo

enum class ChainInfoConstants(val chainInfo: ChainInfo) {

    EtherScan(
        chainInfo = ChainInfo.Ethereum("1", "", "", "Paste Your Api Key"),
    ),
    GiantSquid(
        chainInfo = ChainInfo.Simple("48239ef607d7928874027a43a67689209727dfb3d3dc5e5b03a39bdc2eda771a")
    ),
    OkLink(
        chainInfo = ChainInfo.OkLink("195", "okb", "Paste Your Api Key")
    ),
    Reef(
        chainInfo = ChainInfo.Simple("7834781d38e4798d548e34ec947d19deea29df148a7bf32484b7b24dacf8d4b7")
    ),
    SoraStage(
        chainInfo = ChainInfo.Simple("3266816be9fa51b32cfea58d3e33ca77246bc9618595a4300e44c8856a8d8a17")
    ),
    SoraProd(
        chainInfo = ChainInfo.Simple("7e4e32d0feafd4f9c9414b0be86373f9a1efa904809b683453a9af6856d38ad5")
    ),
    SoraTst(
        chainInfo = ChainInfo.Simple("15b8706309a2407e6ae6615b44f08c2bee8eba3fe54a6a765e4068373feaf269")
    ),
    SubSquid(
        chainInfo = ChainInfo.Simple("91b171bb158e2d3848fa23a9f1c25182fb8e20313b2c1eb49219da7a70ce90c3")
    ),
    SubQuery(
        chainInfo = ChainInfo.Simple("cd4d732201ebe5d6b014edda071c4203e16867305332301dc8d092044b28e554")
    ),
    Zeta(
        chainInfo = ChainInfo.Zeta("7001", "", "")
    )

}