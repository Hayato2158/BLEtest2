//package com.example.bletest
//
//import android.app.Service
//import android.os.Bundle
//import androidx.appcompat.app.AppCompatActivity
//
//class BackgroundService : AppCompatActivity() {
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.)
//
//        Log.d("isActivate", SoundManagerService.isActive.toString())
//
//        if (SoundManagerService.isActive) {
//            buttonStart.isEnabled = false
//            buttonStop.isEnabled = true
//        } else {
//            buttonStart.isEnabled = true
//            buttonStop.isEnabled = false
//        }
//
//        buttonStart.setOnClickListener {
//            val intent = Intent(this, SoundManagerService::class.java)
//            startForegroundService(intent)
//
//            buttonStart.isEnabled = false
//            buttonStop.isEnabled = true
//        }
//
//        buttonStop.setOnClickListener {
//            val intent = Intent(this, SoundManagerService::class.java)
//            stopService(intent)
//
//            buttonStart.isEnabled = true
//            buttonStop.isEnabled = false
//        }
//    }
//
//    override fun onDestroy() {
//        super.onDestroy()
//        Log.d("SoundManagerActivity", "onDestroy")
//    }
//}
//
