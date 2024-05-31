package jp.co.soramitsu.xnetworking.android

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import jp.co.soramitsu.xnetworking.lib.datasources.txhistory.api.models.TxFilter
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.util.concurrent.TimeUnit

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyApplicationTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    MainScreen()
                }
            }
        }
    }
}

@Composable
private fun MainScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        val assetsInfo = LocalContext.current.assets
        Button(
            onClick = {
                GlobalScope.launch {
                    val drs = assetsInfo.open("drop.txt").bufferedReader().use { it.readText() }.let {
                        Json.decodeFromString<List<String>>(it)
                    }
                    try {
                        Log.e("foxxx", "r start btn 1")
                        val r = DepBuilder.blockExplorerRepository.getAssetsInfo(
                            ChainInfoConstants.Sora.chainInfo.chainId,
                            drs,
                            (TimeUnit.SECONDS.convert(System.currentTimeMillis(), TimeUnit.MILLISECONDS) - 24 * 60 * 60).toInt()
                        )
                        Log.e("foxxx", "r = ${r.size}")
                        Log.e("foxxx", "r = ${r}")
                    } catch (t: Throwable) {
                        Log.e("foxxx", "t= ${t.localizedMessage}")
                    }
                }
            },
            content = {
                Text(text = "btn1")
            },
        )
        Spacer(modifier = Modifier.size(8.dp))
        Row {
            var page by remember { mutableStateOf("1") }
            var res by remember { mutableStateOf("") }
            Column(modifier = Modifier.weight(2f)) {
                TextField(
                    value = page,
                    onValueChange = { new ->
                        page = new
                    },
                )
                Text(text = res)
            }
            Button(
                modifier = Modifier.weight(1f),
                onClick = {
                    GlobalScope.launch {
                        Log.e("foxxx", "r start btn 2")
                        try {
                            val r = DepBuilder.historyRemoteLoaderFacade.loadHistoryInfo(
                                pageCount = 1,
                                cursor = page,
                                signAddress = "paste your OkLink address",
                                chainInfo = ChainInfoConstants.OkLink.chainInfo,
                                filters = setOf(TxFilter.TRANSFER)
                            )
                            Log.e(
                                "foxxx",
                                "r = ${r.items}"
                            )
                            res = "${r.endReached}; ${page}; ${r.items.size}"
                        } catch (t: Throwable) {
                            res = t.localizedMessage ?: "Throwable"
                            Log.e("foxxx", "t= ${t.localizedMessage}")
                        }
                    }
                },
                content = {
                    Text(text = "btn2")
                },
            )
        }

        Spacer(modifier = Modifier.size(8.dp))
        Button(
            onClick = {
                GlobalScope.launch {
                    Log.e("foxxx", "r start btn 3")
                    try {
//                        val r = DepBuilder.blockExplorerRepository.getFiat(ChainAssetConstants.Sora.chainId)
                        val r = DepBuilder.blockExplorerRepository.getApy(ChainInfoConstants.Sora.chainInfo.chainId)
//                        val r = DepBuilder.blockExplorerRepository.getReferrerRewards(ChainAssetConstants.Sora.chainId, "")
                        Log.e("foxxx", "r = $r")
                    } catch (t: Throwable) {
                        Log.e("foxxx", "t = ${t.localizedMessage}")
                    }
                }
            },
            content = {
                Text(text = "btn3")
            },
        )
//        Spacer(modifier = Modifier.size(8.dp))
//        Button(
//            onClick = {
//                GlobalScope.launch {
//                    Log.e("foxxx", "r start btn 4")
//                    try {
////                        val url = "https://raw.githubusercontent.com/arvifox/arvifoxandroid/develop/felete/mwr/chains.json"
//                        val url = "http://www.arvifox.com/soramitsu/chains.json"
//                        val r = DepBuilder.soraNetworkClient.createJsonRequest<List<ChainDto>>(url)
//                        Log.e("foxxx", "r = $r")
//                    } catch (t: Throwable) {
//                        Log.e("foxxx", "t = ${t.localizedMessage}")
//                    }
//                }
//            },
//            content = {
//                Text(text = "btn4")
//            },
//        )
    }
}

@Serializable
data class ChainDto(
    val disabled: Boolean,
    val chainId: String,
)

@Composable
fun GreetingView(text: String) {
    Text(text = text)
}

@Preview
@Composable
fun DefaultPreview() {
    MyApplicationTheme {
        GreetingView("Hello, Android!")
    }
}
