package com.kg.gettransfer.presentation.ui.helpers

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Context
import android.os.Build
import com.kg.gettransfer.common.BoundDatePickerDialog
import com.kg.gettransfer.common.BoundTimePickerDialog
import org.koin.core.KoinComponent
import java.util.*

object DateTimePickerHelper : KoinComponent {

    fun showDatePickerDialog(context: Context, selectedDate: Calendar, current: Calendar, handler: DateTimeHandler) {
        val calendar: Calendar = selectedDate
        val boundDatePickerDialog = BoundDatePickerDialog(context,
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH),
            DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
                calendar.set(year, monthOfYear, dayOfMonth)
                handler.onDateChosen(calendar.time)
                val calendarWithoutTime = Calendar.getInstance()
                calendarWithoutTime.set(year, monthOfYear, dayOfMonth, 0, 0)
                when {
                    calendarWithoutTime.time.after(current.time) -> showTimePickerDialog(
                        context,
                        calendar,
                        -1,
                        24,
                        calendar.get(Calendar.HOUR_OF_DAY),
                        calendar.get(Calendar.MINUTE),
                        handler
                    )

                    calendar.time.after(current.time) ->
                        showTimePickerDialog(
                            context,
                            calendar,
                            current.get(Calendar.HOUR_OF_DAY),
                            current.get(Calendar.MINUTE),
                            calendar.get(Calendar.HOUR_OF_DAY),
                            calendar.get(Calendar.MINUTE),
                            handler
                        )

                    else -> showTimePickerDialog(
                        context,
                        calendar,
                        current.get(Calendar.HOUR_OF_DAY),
                        current.get(Calendar.MINUTE),
                        current.get(Calendar.HOUR_OF_DAY),
                        current.get(Calendar.MINUTE),
                        handler
                    )
                }
            })

        if (Build.VERSION.SDK_INT < 21) {
            boundDatePickerDialog.datePicker.minDate = System.currentTimeMillis() - 1000
            boundDatePickerDialog.setMin(
                current.get(Calendar.YEAR),
                current.get(Calendar.MONTH),
                current.get(Calendar.DAY_OF_MONTH)
            )
        } else {

            boundDatePickerDialog.datePicker.minDate = current.timeInMillis
        }
        boundDatePickerDialog.show()
    }

    private fun showTimePickerDialog(
        context: Context,
        selectedDate: Calendar,
        minHour: Int,
        minMinute: Int,
        setHour: Int,
        setMinute: Int,
        handler: DateTimeHandler
    ) {
        BoundTimePickerDialog(
            context,
            @Suppress("DEPRECATION") AlertDialog.THEME_HOLO_LIGHT,
            { _, hour, minute ->
                selectedDate.set(Calendar.HOUR_OF_DAY, hour)
                selectedDate.set(Calendar.MINUTE, minute)
                handler.onTimeChosen(selectedDate.time)
            },
            setHour,
            setMinute,
            true
        ).apply {
            setMin(minHour, minMinute)
            show()
        }
    }
}
