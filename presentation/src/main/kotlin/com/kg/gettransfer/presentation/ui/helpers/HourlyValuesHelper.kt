package com.kg.gettransfer.presentation.ui.helpers

import android.content.Context
import com.kg.gettransfer.R

object HourlyValuesHelper {

    lateinit var durationValues: ArrayList<Int>


    fun getHourlyValues(context: Context): ArrayList<String> {
        val possibleMinutes = intArrayOf(120, 180, 240, 300, 360, 480, 600, 1440, 2880, 4320, 5760, 7200, 14400, 21600, 43200)
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
        var stringValue: String
        hours.let {
            stringValue = "$it " + context.resources.getQuantityString(R.plurals.hours, it, it)
            if (it > 10) {
                val days = it / 24
                stringValue = "$days " + context.resources.getQuantityString(R.plurals.days, days, days)
            }
            return stringValue
        }
    }
}