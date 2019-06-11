package com.kg.gettransfer.common

import android.app.TimePickerDialog
import android.content.Context
import android.widget.NumberPicker
import android.widget.TimePicker
import com.kg.gettransfer.R

class BoundTimePickerDialog(
    context: Context,
    themeId: Int,
    callBack: (TimePicker, Int, Int) -> Unit,
    hourOfDay: Int,
    minute: Int,
    is24HourView: Boolean
) : TimePickerDialog(context, themeId, callBack, hourOfDay, minute, is24HourView) {

    private var minHour = -1
    private var minMinute = -1
    private var maxHour = 24
    private var maxMinute = 60

    private var currentHour = hourOfDay
    private var currentMinute = minute

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()

        try {
            val hourId = context.resources.getIdentifier("android:id/hour", null, null)
            val hourDivider = findViewById<NumberPicker>(hourId)
            hourDivider.setDividerColor(R.color.primaryDriver)
            val minuteId = context.resources.getIdentifier("android:id/minute", null, null)
            val minuteDivider = findViewById<NumberPicker>(minuteId)
            minuteDivider.setDividerColor(R.color.primaryDriver)
        } catch (e: Exception) {
        }
    }

    private fun NumberPicker.setDividerColor(color: Int) {
        val dividerField =
            NumberPicker::class.java.declaredFields.firstOrNull { it.name == "mSelectionDivider" } ?: return
        try {
            dividerField.isAccessible = true
            dividerField.set(this, resources.getDrawable(color))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun setMin(hour: Int, minute: Int) {
        minHour = hour
        minMinute = minute
    }

    fun setMax(hour: Int, minute: Int) {
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

        if (validTime) {
            currentHour = hourOfDay
            currentMinute = minute
        } else {
            currentHour = minHour
            currentMinute = minMinute
        }

        updateTime(currentHour, currentMinute)
    }
}