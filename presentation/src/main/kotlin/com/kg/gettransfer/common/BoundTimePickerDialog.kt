package com.kg.gettransfer.common

import android.app.TimePickerDialog
import android.widget.TimePicker
import com.kg.gettransfer.presentation.ui.CreateOrderActivity

class BoundTimePickerDialog(context: CreateOrderActivity,
                            callBack: (TimePicker, Int, Int) -> Unit,
                            hourOfDay: Int,
                            minute: Int,
                            is24HourView: Boolean): TimePickerDialog(context, callBack, hourOfDay, minute, is24HourView) {

    private var minHour = -1
    private var minMinute = -1
    private var maxHour = 24
    private var maxMinute = 60

    private var currentHour = hourOfDay
    private var currentMinute = minute

    fun setMin(hour: Int, minute: Int){
        minHour = hour
        minMinute = minute
    }

    fun setMax(hour: Int, minute: Int){
        maxHour = hour
        maxMinute = minute
    }

    override fun onTimeChanged(view: TimePicker?, hourOfDay: Int, minute: Int) {
        super.onTimeChanged(view, hourOfDay, minute)

        val validTime: Boolean = when {
            hourOfDay < minHour -> false
            hourOfDay == minHour -> minute >= minMinute
            hourOfDay == maxHour -> minute <= maxMinute
            else -> true
        }

        if(validTime){
            currentHour = hourOfDay
            currentMinute = minute
        } else {
            currentHour = minHour
            currentMinute = minMinute
        }

        updateTime(currentHour, currentMinute)
    }
}