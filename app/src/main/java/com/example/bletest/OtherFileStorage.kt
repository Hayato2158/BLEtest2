package com.example.bletest

import android.content.Context
import android.os.Bundle
import android.os.Environment
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.BufferedWriter
import java.io.FileWriter
import java.io.PrintWriter
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class OtherFileStorage private constructor(private val context: Context) {
    private val fileAppend: Boolean = true //true=追記, false=上書き
    private var fileName: String = DateUtils.getNowDate().plus("EnvsensData")
    private val extension: String = ".csv"
    private val filePath: String =
        context.getApplicationContext().getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)
            .toString().plus("/").plus(fileName).plus(extension) //内部ストレージのDocumentのURL


    init {
        val fileDate: String = DateUtils.getNowDate()
        val fil = FileWriter(filePath, fileAppend)
        val pw = PrintWriter(BufferedWriter(fil))
        val addUnixTimeCsv = "time,temperature,humidity,Light,UV,Pressure,Noise,Discomfort,WBGT"
        pw.println(addUnixTimeCsv)
        pw.close()
    }

    fun writeText(text: String) {
        val fil = FileWriter(filePath, fileAppend)
        val pw = PrintWriter(BufferedWriter(fil))
        val nowDate = DateUtils.getNowDate()
        val addUnixTimeCsv = nowDate.plus(",").plus(text)
        pw.println(addUnixTimeCsv)
        pw.close()
    }

    companion object {
        private var instance: OtherFileStorage? = null

        fun getInstance(context: Context): OtherFileStorage {
            if (instance == null) {
                instance = OtherFileStorage(context)
            }
            return instance!!
        }
    }
}