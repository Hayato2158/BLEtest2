package com.example.bletest

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.bletest.ui.theme.BLEtestTheme
import android.util.Log


class MainActivity : ComponentActivity() {
    var getBLE = GetBLE(context = this)
    lateinit var OtherFileStorage: OtherFileStorage

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d("BLEtest", "onCreate")
        super.onCreate(savedInstanceState)

        getBLE.startScan()

        enableEdgeToEdge()
        setContent {
            BLEtestTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Greeting(
                        name = "Android",
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}


@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "BLE通信を受信しています",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    BLEtestTheme {
        Greeting("Android")
    }
}