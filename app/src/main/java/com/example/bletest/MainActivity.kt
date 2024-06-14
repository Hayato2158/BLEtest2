package com.example.bletest

import android.Manifest
import android.content.pm.PackageManager
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
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.bletest.ui.theme.BLEtestTheme
import android.util.Log

class MainActivity : ComponentActivity() {
    var getBLE = GetBLE(context = this)
    lateinit var otherFileStorage: OtherFileStorage

    companion object {
        private const val REQUEST_CODE_BLUETOOTH = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d("BLEtest", "onCreate")
        super.onCreate(savedInstanceState)

        // パーミッションのチェックとリクエスト
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                arrayOf(
                    Manifest.permission.BLUETOOTH_SCAN,
                    Manifest.permission.BLUETOOTH_CONNECT,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ), REQUEST_CODE_BLUETOOTH)
        } else {
            // パーミッションが既に付与されている場合はスキャンを開始
            getBLE.startScan()
        }

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

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_BLUETOOTH) {
            if (grantResults.isNotEmpty() && grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
                // パーミッションが付与された場合はスキャンを開始
                getBLE.startScan()
            } else {
                // パーミッションが拒否された場合の処理
                Log.d("BLEtest", "Bluetooth permissions are required for this app to function properly.")
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
