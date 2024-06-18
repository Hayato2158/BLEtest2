package com.example.bletest

import android.icu.text.SimpleDateFormat
import java.util.*

class DateUtils {

    companion object{
        fun getNowDate(): String {
            val df = SimpleDateFormat("yyyy-MM-dd-HH-mm-ss", Locale.US)
            val date = Date(System.currentTimeMillis())
            return df.format(date)
        }

        fun getTimeStamp():Long {
            return System.currentTimeMillis()
        }
    }
}