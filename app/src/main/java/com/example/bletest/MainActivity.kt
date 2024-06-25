package com.example.bletest

import android.Manifest
import android.bluetooth.le.ScanSettings
import android.content.Intent
import android.content.pm.PackageManager
import android.media.MediaDrm.LogMessage
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.os.PowerManager
import android.provider.Settings
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
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import pub.devrel.easypermissions.EasyPermissions;

class MainActivity : ComponentActivity() {


    var getBLE = GetBLE(context = this)
    lateinit var otherFileStorage: OtherFileStorage
    private val PERMISSION_REQUEST_CODE = 1

    private lateinit var handler: Handler
    private lateinit var scanRunnable: Runnable
    private val scanInterval: Long = 30000 // 60sec　いい感じに変えてもいい
    private var isScanning: Boolean = false
    val bleForegroundService = BleForegroundService()

    // ログメッセージの状態変数を追加
    private val logMessage = mutableStateOf("")

    companion object {
        private const val REQUEST_CODE_BLUETOOTH = 1
    }

    // パーミッションのチェックとリクエスト
    val permissions = if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.S){
        arrayOf(
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.BLUETOOTH_CONNECT,
            Manifest.permission.BLUETOOTH_SCAN,
        )
    }else{
        arrayOf(
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {

        //csv書き込み関数初期化
        otherFileStorage = OtherFileStorage.getInstance(context = this)  // 追加された部分

        val scanSettings = ScanSettings.Builder()
            .setScanMode(ScanSettings.SCAN_MODE_LOW_POWER)
            .build()





//        if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED ||
//            ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED ||
//            ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(this,
//                arrayOf(
//                    Manifest.permission.BLUETOOTH_SCAN,
//                    Manifest.permission.BLUETOOTH_CONNECT,
//                    Manifest.permission.ACCESS_FINE_LOCATION
//                ), REQUEST_CODE_BLUETOOTH)
//        } else {
//            // パーミッションが既に付与されている場合はスキャンを開始
//
//        }


        super.onCreate(savedInstanceState)

        if(!EasyPermissions.hasPermissions(this, *permissions)){
            //パーミッションが許可されていない時の処理
            EasyPermissions.requestPermissions(this, "パーミッションに関する説明", PERMISSION_REQUEST_CODE, *permissions)
        }

        requestIgnoreBatteryOptimizations()


//        getBLE.startScan(emptyList(),scanSettings)
        logMessage.value = "BLEスキャン成功"
        Log.d("BLEtest", "onCreateした")

        // ハンドラとランナブルの初期化
        handler = Handler(Looper.getMainLooper())

        scanRunnable = object : Runnable {
            override fun run() {
                if (isScanning) {
                    // スキャン中であればスキャンを停止
                    getBLE.stopScan()
                    Log.d("BLEtest", "スキャン停止")
                } else {

                    // スキャン中でなければスキャンを開始
//                    getBLE.startScan()
//                    Log.d("BLEtest", "スキャン開始")

                    val serviceIntent = Intent(application, BleForegroundService::class.java)
                    startForegroundService(serviceIntent)
                    Log.d("BLEtest", "セントラルサービスでスキャン開始")

                }
                // スキャン状態を反転
                isScanning = !isScanning
                // 次回の実行を60秒後に設定
                handler.postDelayed(this, scanInterval)
            }
        }

        // スキャンプロセスの開始
        //パーミッションがない場合は止める
        if(EasyPermissions.hasPermissions(this, *permissions)){
            handler.post(scanRunnable)
        }else{
            EasyPermissions.requestPermissions(this, "パーミッションに関する説明", PERMISSION_REQUEST_CODE, *permissions)
        }







//        //サービスの開始
//        val serviceIntent = Intent(this, MyForegroundService::class.java)
//        ContextCompat.startForegroundService(this, serviceIntent)
//        Log.d("ForegroundService", "サービス実行されてるっぽいよ")


        enableEdgeToEdge()
        setContent {
            BLEtestTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    //通知とフォアグランドでの起動
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

                handler.post(scanRunnable)
                val logMessage = "Bluetooth permissions granted\n"
                with(NotificationManagerCompat.from(this))
                         {
                    if (androidx.core.app.ActivityCompat.checkSelfPermission(
                            this@MainActivity,
                            android.Manifest.permission.POST_NOTIFICATIONS
                        ) != android.content.pm.PackageManager.PERMISSION_GRANTED
                    ) {
                        return@with
                    }
                }

            } else {
                // パーミッションが拒否された場合の処理
                Log.d("BLEtest", "Bluetooth permissions are required for this app to function properly.\n")
            }
        }
    }

    private fun requestIgnoreBatteryOptimizations() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val intent = Intent()
            val packageName = packageName
            val pm = getSystemService(PowerManager::class.java)
            if (!pm.isIgnoringBatteryOptimizations(packageName)) {
                intent.action = Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS
                intent.data = Uri.parse("package:$packageName")
                startActivity(intent)
            }
        }
    }




}




@Composable
fun Greeting(modifier: Modifier = Modifier, logMessage: String) {

Column(modifier = modifier) {
    Text(
        text = "アプリケーションが実行されました",
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
