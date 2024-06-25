package com.example.bletest

import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanFilter
import android.bluetooth.le.ScanResult
import android.bluetooth.le.ScanSettings
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.*
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationChannelCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

class BleForegroundService : Service() {

    companion object {
        const val CHANNEL_ID = "bbb123"
        const val CHANNEL_TITLE = "セントラル中"
    }

    private lateinit var notificationManager: NotificationManagerCompat
    private lateinit var getBLE: GetBLE
    private lateinit var handler: Handler
    private lateinit var scanRunnable: Runnable
    private val scanInterval: Long = 60000 // 60秒
    private var isScanning: Boolean = false
    private lateinit var wakeLock: PowerManager.WakeLock

    override fun onCreate() {
        Log.d("Service", "サービス開始するよ")
        getBLE = GetBLE(context = this) //初期化


//        val myScanCallback = object : ScanCallback() {
//            override fun onScanResult(callbackType: Int, result: ScanResult?) {
//                super.onScanResult(callbackType, result)
//
//
//                Log.d("BLEtest", "result: $result")
//            }
//        }

        super.onCreate()

        notificationManager = NotificationManagerCompat.from(this)
        val channel = NotificationChannelCompat.Builder(
            CHANNEL_ID,
            NotificationManagerCompat.IMPORTANCE_MAX
        )
            .setName("環境センサからデータ受信中")
            .build()
        notificationManager.createNotificationChannel(channel)

        // 通知の構築
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("EnvMonitor")
            .setContentText("あなたの生活空間をセンシングしています")
            .setOngoing(true)
            .build()

        val scanSettings = ScanSettings.Builder()
            .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
            .build()


        val scanFilter: ScanFilter =
            ScanFilter.Builder()
                .build()

        val scanFilters = mutableListOf<ScanFilter>()
        scanFilters.add(scanFilter)

        startForeground(2222,notification)
        //受信開始
//        getBLE.setScancallback(myScanCallback)
        Log.d("BLEtest","scanFilters: $scanFilters , scanSettings: $scanSettings")
        getBLE.startScan(scanFilters, scanSettings)
        Log.d("BLEtest", "サービスでBLEスキャン成功")

        loopBLEscan()
        Log.d("BLEtest", "サービスでLoopBLEscan成功")
    }


    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("BleForegroundService", "サービス終了")

        // WakeLock の解放
        wakeLock.release()
        // スキャン処理の停止
        handler.removeCallbacks(scanRunnable)
    }

    private fun loopBLEscan(){

        // ハンドラとランナブルの初期化
        handler = Handler(Looper.getMainLooper())
        scanRunnable = object : Runnable {
            override fun run() {
                if (isScanning) {
                    getBLE.stopScan()
                } else {
                    getBLE.startScan()
                }
                isScanning = !isScanning

                handler.postDelayed(this, scanInterval)
            }
        }
    }
}