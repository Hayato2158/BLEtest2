package com.example.bletest

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.le.BluetoothLeScanner
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanFilter
import android.bluetooth.le.ScanResult
import android.bluetooth.le.ScanSettings
import android.content.Context
import android.util.Log

class GetBLE(private val context: Context) {
    //class GetBLE(context: Context) {
    private val bluetoothAdapter: BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter()
    private val bluetoothLeScanner: BluetoothLeScanner? = bluetoothAdapter?.bluetoothLeScanner

    //    private val context: Context = context
    private var scanCallback: ScanCallback? = null

    fun setScancallback(callback: ScanCallback) {
        this.scanCallback = callback
    }


    var OutputData: String = ""


    fun ByteArray.toHex(): String =
        joinToString(separator = "") { eachByte -> "%02x".format(eachByte) }

    @SuppressLint("MissingPermission")
    fun startScan(
        scanFilter: List<ScanFilter>? = null,
        scanSettings: ScanSettings? = null
    ) {

        val otherFileStorage = OtherFileStorage.getInstance(context)

        bluetoothLeScanner?.let { scanner ->
//            scanCallback?.let { callback ->
//                scanner.startScan(scanFilter, scanSettings, callback)
//            }
            if (scanCallback == null) {
                scanCallback = object : ScanCallback() {
                    override fun onScanResult(callbackType: Int, result: ScanResult) {
                        super.onScanResult(callbackType, result)
                        val device: BluetoothDevice = result.device
                        val uuids = result.scanRecord?.serviceUuids
                        val receiveRssi = result.rssi
//                        Log.d("output",OutputData)

                        val sensAdvDataList = result.scanRecord?.advertisingDataMap
                        var sensAdvDataString = ""

//                        if (device.address == "FB:6E:DF:AF:49:A9") { // USB
                        if (device.address == "FB:D6:56:B7:F8:1E") {// BUG
                            var logMessage =
                                "Device: ${device.address} , 取り出したいデータ: $OutputData , RSSI: $receiveRssi"

                            uuids?.forEach { uuid ->
                                val uuidString = uuid.uuid.toString()
                                logMessage += "UUID: $uuidString"
                            }

                            Log.d(device.address, logMessage) // リアルタイムでログに出力

                            if (sensAdvDataList != null) {
                                for (sensData in sensAdvDataList) {
                                    sensAdvDataString += " value: ${sensData.value.toHex()},key: ${sensData.key}" //これが取り出したいデータ
                                    if (sensData.key == 255) {
                                        OutputData = sensData.value.toHex()




                                        //温度(6~9)
                                        val tempB = OutputData.substring(6, 8)
                                        val tempF = OutputData.substring(8, 10)
                                        val temp16 = tempF + tempB
                                        //16→10進数変換
                                        val temp10 =
                                            Integer.parseInt(temp16, 16).toShort().toString()
                                        //小数点ぶち込む(/100)
                                        val temp =
                                            temp10.substring(0, 2) + "." + temp10.substring(2)


                                        //湿度(10~13)
                                        val humB = OutputData.substring(10, 12)
                                        val humF = OutputData.substring(12, 14)
                                        val hum16 = humF + humB
                                        //16→10進数変換
                                        val hum10 = Integer.parseInt(hum16, 16).toShort().toString()
                                        //小数点ぶち込む(/100)
                                        val hum = hum10.substring(0, 2) + "." + hum10.substring(2)


                                        // 照度(14~17)
                                        val lightB = OutputData.substring(14, 16)
                                        val lightF = OutputData.substring(16, 18)
                                        val light16 = lightF + lightB
                                        //16→10進数変換
                                        val light =
                                            Integer.parseInt(light16, 16).toShort().toString()

                                        // 紫外線(18~21)
                                        val uvB = OutputData.substring(18, 20)
                                        val uvF = OutputData.substring(20, 22)
                                        val uv16 = uvF + uvB
                                        //16→10進数変換
                                        val uv = Integer.parseInt(uv16, 16).toShort().toString()


                                        // 気圧(22~25)
                                        val presB = OutputData.substring(22, 24)
                                        val presF = OutputData.substring(24, 26)
                                        val pres16 = presF + presB
                                        //16→10進数変換
                                        val pres10 = Integer.parseInt(pres16, 16).toShort().toInt()
                                        //小数点ぶち込む(/100)
                                        val pres: String = (pres10 / 10).toString()

                                        // 騒音(26~29)
                                        val noiseB = OutputData.substring(26, 28)
                                        val noiseF = OutputData.substring(28, 30)
                                        val noise16 = noiseF + noiseB
                                        //16→10進数変換
                                        val noise10 =
                                            Integer.parseInt(noise16, 16).toShort().toString()
                                        //小数点ぶち込む(/100)
                                        val noise =
                                            noise10.substring(0, 2) + "." + noise10.substring(2)


                                        // 不快指数(30~33)
                                        val discomfortB = OutputData.substring(30, 32)
                                        val discomfortF = OutputData.substring(32, 34)
                                        val discomfort16 = discomfortF + discomfortB
                                        //16→10進数変換
                                        val discomfort10 =
                                            Integer.parseInt(discomfort16, 16).toShort().toString()
                                        //小数点ぶち込む(/100)
                                        val discomfort = discomfort10.substring(
                                            0,
                                            2
                                        ) + "." + discomfort10.substring(2)


                                        // 熱中症症状(34~37)
                                        val wbgtB = OutputData.substring(34, 36)
                                        val wbgtF = OutputData.substring(36, 38)
                                        val wbgt16 = wbgtF + wbgtB
                                        //16→10進数変換
                                        val wbgt10 =
                                            Integer.parseInt(wbgt16, 16).toShort().toString()
                                        //小数点ぶち込む(/100)
                                        val wbgt =
                                            wbgt10.substring(0, 2) + "." + wbgt10.substring(2)


                                        otherFileStorage.writeText("$temp,$hum,$light,$uv,$pres,$noise,$discomfort,$wbgt") //変換後のデータをcsvに書き込む


                                    }
                                }
                            }
                        }
                        //USB型の環境センサif文ここに書く
                    }

                    override fun onScanFailed(errorCode: Int) {
                        super.onScanFailed(errorCode)
                        Log.e("GetBLE", "Scan failed with error code $errorCode")
                    }
                }

                scanner.startScan(scanFilter, scanSettings, scanCallback)
                Log.d("GetBLE", "scan started")
            } else {
                Log.d("GetBLE", "Scan already running")
            }
        } ?: run {
            Log.e("GetBLE", "BluetoothLeScanner is not initialized")
        }
    }

    //ボタン押したらスキャン止める機能を足す時に使う
    @SuppressLint("MissingPermission")
    fun stopScan() {
        scanCallback?.let { bluetoothLeScanner?.stopScan(it) }
        scanCallback = null
        Log.d("GetBLE", "scan stopped")
    }

}



