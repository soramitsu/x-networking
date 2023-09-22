package jp.co.soramitsu.appxnetworking

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        DepBuilder.build(applicationContext)
        setContent {
            XNetworkingComposeTheme {
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
        Button(
            onClick = {
                GlobalScope.launch {
                    try {
                        Log.e("foxxx", "r start btn 1")
                        val r = DepBuilder.networkService.getAssetsInfo()
                        Log.e("foxxx", "r = ${r}")
                    } catch (t: Throwable) {
                        Log.e("foxxx", "t= ${t.localizedMessage}")
                        t.printStackTrace()
                    }
                }
            },
            content = {
                Text(text = "Custom BlockExplorer #2")
            },
        )
        Spacer(modifier = Modifier.size(8.dp))
        Button(
            onClick = {
                GlobalScope.launch {
                    Log.e("foxxx", "r start btn 2")
                    try {
                        val r = DepBuilder.networkService.getHistoryFearless(1)
                        Log.e(
                            "foxxx",
                            "r = ${r.endReached} ${r.page} ${r.items.size} ${r.errorMessage}"
                        )
                    } catch (t: Throwable) {
                        Log.e("foxxx", "t= ${t.localizedMessage}")
                        t.printStackTrace()
                    }
                }
            },
            content = {
                Text(text = "Get TxHistoryItems")
            },
        )
        Spacer(modifier = Modifier.size(8.dp))
        Button(
            onClick = {
                GlobalScope.launch {
                    Log.e("foxxx", "r start btn 2")
                    try {
                        val r = DepBuilder.networkService.getPeers("")
                        Log.e(
                            "foxxx",
                            "r = ${r.size}"
                        )
                    } catch (t: Throwable) {
                        Log.e("foxxx", "t= ${t.localizedMessage}")
                        t.printStackTrace()
                    }
                }
            },
            content = {
                Text(text = "Get Peers")
            },
        )
        Spacer(modifier = Modifier.size(8.dp))
        Button(
            onClick = {
                GlobalScope.launch {
                    Log.e("foxxx", "r start btn 3")
                    try {
                        val r = DepBuilder.networkService.getSoraConfig()
                        Log.e("foxxx", "r = $r")
                    } catch (t: Throwable) {
                        Log.e("foxxx", "t = ${t.localizedMessage}")
                        t.printStackTrace()
                    }
                }
            },
            content = {
                Text(text = "btn3")
            },
        )
    }
}
