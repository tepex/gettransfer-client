package com.kg.gettransfer.presentation.ui.helpers

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Context
import android.os.Build
import com.kg.gettransfer.common.BoundDatePickerDialog
import com.kg.gettransfer.common.BoundTimePickerDialog
import org.koin.standalone.KoinComponent
import java.util.*

object DateTimePickerHelper : KoinComponent {
    private val calendar: Calendar = Calendar.getInstance()

    fun showDatePickerDialog(context: Context, current: Calendar, handler: DateTimeHandler) {
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
                        -1,
                        24,
                        calendar.get(Calendar.HOUR_OF_DAY),
                        calendar.get(Calendar.MINUTE),
                        handler
                    )

                    calendar.time.after(current.time) ->
                        showTimePickerDialog(
                            context,
                            current.get(Calendar.HOUR_OF_DAY),
                            current.get(Calendar.MINUTE),
                            calendar.get(Calendar.HOUR_OF_DAY),
                            calendar.get(Calendar.MINUTE),
                            handler
                        )

                    else -> showTimePickerDialog(
                        context,
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
        minHour: Int,
        minMinute: Int,
        setHour: Int,
        setMinute: Int,
        handler: DateTimeHandler
    ) {
        BoundTimePickerDialog(
            context,
            AlertDialog.THEME_HOLO_LIGHT,
            { _, hour, minute ->
                calendar.set(Calendar.HOUR_OF_DAY, hour)
                calendar.set(Calendar.MINUTE, minute)
                handler.onTimeChosen(calendar.time)
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
