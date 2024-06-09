package com.example.bletest

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.le.BluetoothLeScanner
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.bluetooth.le.ScanSettings
import android.util.Log

class GetBLE {
    private val bluetoothAdapter: BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter()
    private val bluetoothLeScanner: BluetoothLeScanner? = bluetoothAdapter?.bluetoothLeScanner
    private var scanCallback: ScanCallback? = null

    fun ByteArray.toHex(): String = joinToString(separator = "") { eachByte -> "%02x".format(eachByte) }

    @SuppressLint("MissingPermission")
    fun startScan() {
        val scanSetting = ScanSettings.SCAN_MODE_BALANCED

        bluetoothLeScanner?.let { scanner ->
            if (scanCallback == null) {
                scanCallback = object : ScanCallback() {
                    override fun onScanResult(callbackType: Int, result: ScanResult) {
                        super.onScanResult(callbackType, result)
                        val device: BluetoothDevice = result.device
                        val uuids = result.scanRecord?.serviceUuids
                        val receiveRssi = result.rssi


                        val sensDataList = result.scanRecord?.serviceData
                        var sensDataString = ""
                        if (sensDataList != null) {
                            for (sensData in sensDataList ){
                                sensDataString += ",key: ${sensData.key}, value: ${sensData.value.toHex()}"
                            }
                        }

                        val sensAdvDataList = result.scanRecord?.advertisingDataMap
                        var sensAdvDataString = ""
                        if (sensAdvDataList != null) {
                            for (sensData in sensAdvDataList ){
                                sensAdvDataString += ",key: ${sensData.key}, value: ${sensData.value.toHex()}"
                            }
                        }

                        var logMessage =
                            "Device: ${device.address} , RSSI: $receiveRssi ,sensData: $sensDataString, sensAdvData: $sensAdvDataString "
                        uuids?.forEach { uuid ->
                            val uuidString = uuid.uuid.toString()
                            logMessage += "UUID: $uuidString"
                            Log.d(device.address, logMessage) // リアルタイムでログに出力
                        }

                        Log.d(device.address, logMessage) // リアルタイムでログに出力
                    }

                    override fun onScanFailed(errorCode: Int) {
                        super.onScanFailed(errorCode)
                        Log.e("GetBLE", "Scan failed with error code $errorCode")
                    }
                }
                scanner.startScan(scanCallback)
                Log.d("GetBLE", "scan started")
            } else {
                Log.d("GetBLE", "Scan already running")
            }
        } ?: run {
            Log.e("GetBLE", "BluetoothLeScanner is not initialized")
        }
    }


    @SuppressLint("MissingPermission")
    fun stopScan() {
        scanCallback?.let { bluetoothLeScanner?.stopScan(it) }
        scanCallback = null
        Log.d("GetBLE", "scan stopped")
    }
}