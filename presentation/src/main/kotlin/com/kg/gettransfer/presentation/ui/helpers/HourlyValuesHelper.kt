package com.kg.gettransfer.presentation.ui.helpers

import android.content.Context
import com.kg.gettransfer.R

object HourlyValuesHelper {

    lateinit var durationValues: ArrayList<Int>


    fun getHourlyValues(context: Context): ArrayList<String> {
        val possibleMinutes = intArrayOf(120, 180, 240, 300, 360, 480, 600, 1440, 2880, 4320, 5760, 7200, 14400, 21600)
        val displayedValues = ArrayList<String>()
        durationValues = ArrayList()
        possibleMinutes.forEach {
            val hours = it / 60
            val value = getValue(hours, context)
            displayedValues.add(value)
            durationValues.add(hours)
        }
        return displayedValues
    }

    fun getValue(hours: Int, context: Context): String {
        var stringValue = ""
        hours.let {
            when (it) {
                in 2..4  -> stringValue = "$it " + context.getString(R.string.LNG_HOUR_FEW)
                in 5..10 -> stringValue = "$it " + context.getString(R.string.LNG_HOURS)
                else  -> {
                    val days = it / 24
                    when (days) {
                        1        -> stringValue = "$days " + context.getString(R.string.LNG_DAY)
                        in 2..4  -> stringValue = "$days " + context.getString(R.string.LNG_DAYS_FEW)
                        in 5..15 -> stringValue = "$days " + context.getString(R.string.LNG_DAYS)
                    }

                }
            }
            return stringValue
        }
    }
}