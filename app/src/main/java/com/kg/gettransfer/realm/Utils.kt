package com.kg.gettransfer.realm

import android.content.Context
import android.text.format.DateUtils
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by denisvakulenko on 19/02/2018.
 */

object Utils {
    fun dateFromString(s: String): Date? {
        val dtStart = "11/08/2013 08:48:10"
        val format = SimpleDateFormat("MM/dd/yyyy HH:mm:ss")
        try {
            val date = format.parse(dtStart)
            System.out.println("Date ->" + date)
            return date
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return null
    }

    fun dateToString(c: Context, date: Date?): String {
        if (date == null) return "-"
        return DateUtils.formatDateTime(
                c,
                date.time,
                DateUtils.FORMAT_SHOW_TIME).toString() +
                "   " +
                DateUtils.formatDateTime(
                        c,
                        date.time,
                        DateUtils.FORMAT_SHOW_DATE or DateUtils.FORMAT_ABBREV_MONTH or DateUtils.FORMAT_SHOW_YEAR).toString()
    }
}