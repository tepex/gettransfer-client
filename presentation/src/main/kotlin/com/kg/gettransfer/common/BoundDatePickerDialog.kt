package com.kg.gettransfer.common

import android.app.DatePickerDialog
import android.content.Context
import android.widget.DatePicker
import java.util.Calendar

class BoundDatePickerDialog(
    context: Context,
    onDateSetListener: DatePickerDialog.OnDateSetListener,
    year: Int,
    month: Int,
    dayOfMonth: Int
) : DatePickerDialog(context, onDateSetListener, year, month, dayOfMonth) {

    private var minYear: Int? = null
    private var minMonth: Int? = null
    private var minDayOfMonth: Int? = null
    private var calendarMin: Calendar? = null

    private var currentYear = year
    private var currentMonth = month
    private var currentDayOfMonth = dayOfMonth

    fun setMin(year: Int, month: Int, dayOfMonth: Int) {
        minYear = year
        minMonth = month
        minDayOfMonth = dayOfMonth
        calendarMin = Calendar.getInstance()
        calendarMin!!.set(year, month, dayOfMonth)
    }

    override fun onDateChanged(view: DatePicker, year: Int, month: Int, dayOfMonth: Int) {
        super.onDateChanged(view, year, month, dayOfMonth)

        if(calendarMin != null && (currentYear != year || currentMonth != month || currentDayOfMonth != month)) {
            val calendarSelected = Calendar.getInstance()
            calendarSelected.set(year, month, dayOfMonth)

            val validDate = calendarSelected.after(calendarMin)

            if (validDate) {
                currentYear = year
                currentMonth = month
                currentDayOfMonth = dayOfMonth
            } else {
                currentYear = minYear!!
                currentMonth = minMonth!!
                currentDayOfMonth = minDayOfMonth!!
                updateDate(currentYear, currentMonth, currentDayOfMonth)
            }
        }
    }
}
