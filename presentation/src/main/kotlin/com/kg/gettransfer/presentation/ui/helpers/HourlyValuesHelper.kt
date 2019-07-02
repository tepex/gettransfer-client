package com.kg.gettransfer.presentation.ui.helpers

import android.content.Context
import com.kg.gettransfer.R

object HourlyValuesHelper {
    
    var durationValues = arrayListOf(2, 3, 4, 5, 6, 8, 10, 24, 48, 72, 96, 120, 240, 360, 720)

    fun getHourlyValues(context: Context) = durationValues.map { getValue(it, context) }

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