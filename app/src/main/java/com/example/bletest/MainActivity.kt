package com.example.bletest

import android.Manifest
import android.content.pm.PackageManager
import android.media.MediaDrm.LogMessage
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
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
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.mutableStateOf

class MainActivity : ComponentActivity() {
    var getBLE = GetBLE(context = this)
    lateinit var otherFileStorage: OtherFileStorage

    private lateinit var handler: Handler
    private lateinit var scanRunnable: Runnable
    private val scanInterval: Long = 60000 // 60sec　いい感じに変えてもいい
    private var isScanning: Boolean = false

    // ログメッセージの状態変数を追加
    private val logMessage = mutableStateOf("")

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
            logMessage.value = "Bluetooth permissions granted"
        }

        //初期化
        otherFileStorage = OtherFileStorage.getInstance(context = this)  // 追加された部分


        // ハンドラとランナブルの初期化
        handler = Handler(Looper.getMainLooper())
        scanRunnable = object : Runnable {
            override fun run() {
                if (isScanning) {
                    // スキャン中であればスキャンを停止
                    getBLE.stopScan()
                } else {
                    // スキャン中でなければスキャンを開始
                    getBLE.startScan()
                }
                // スキャン状態を反転
                isScanning = !isScanning
                // 次回の実行を60秒後に設定
                handler.postDelayed(this, scanInterval)
            }
        }

        // スキャンプロセスの開始
        handler.post(scanRunnable)

        enableEdgeToEdge()
        setContent {
            BLEtestTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Greeting(
                        modifier = Modifier.padding(innerPadding),
                        logMessage = logMessage.value
                    )
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        // アクティビティが破棄される際にランナブルを停止
        handler.removeCallbacks(scanRunnable)
        // スキャン中であればスキャンを停止
        if (isScanning) {
            getBLE.stopScan()
        }
    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_BLUETOOTH) {
            if (grantResults.isNotEmpty() && grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
                // パーミッションが付与された場合はスキャンを開始
                getBLE.startScan()
                val logMessage = "Bluetooth permissions granted\n"
            } else {
                // パーミッションが拒否された場合の処理
                Log.d("BLEtest", "Bluetooth permissions are required for this app to function properly.\n")
            }
        }
    }


}

@Composable
fun Greeting(modifier: Modifier = Modifier, logMessage: String) {

Column(modifier = modifier) {
    Text(
        text = "BLE通信を受信しています",
        modifier = modifier
    )
    Text(
        text = logMessage,
        modifier = modifier
    )
}





}

//@Preview(showBackground = true)
//@Composable
//fun GreetingPreview(modifier: Modifier = Modifier, logMessage: String) {
//    BLEtestTheme {
//        Text(
//            text = "BLE通信を受信しています\n",
//            modifier = modifier
//        )
//        Text(
//            text = logMessage,
//            modifier = modifier
//
//        )
//    }
//}
