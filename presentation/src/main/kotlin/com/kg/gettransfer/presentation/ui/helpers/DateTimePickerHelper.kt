package com.kg.gettransfer.presentation.ui.helpers

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Context
import android.content.ContextWrapper
import android.content.res.Resources
import android.os.Build
import androidx.annotation.NonNull

import com.kg.gettransfer.common.BoundDatePickerDialog
import com.kg.gettransfer.common.BoundTimePickerDialog

import org.koin.core.KoinComponent

import java.util.Calendar
import java.util.IllegalFormatConversionException

object DateTimePickerHelper : KoinComponent {

    /**
     * It's a Samsung bug in their Lollipop UI implementation. It affects Galaxy S4, S5, Note 3 and probably more devices.
     * For us it occurs on de_DE and de_AT languages, but it appears to be an issue that affects multiple languages.
     *
     * I fixed it by forcing Samsung devices to use the Holo theme for the date picker.
     */
    private fun isBrokenSamsungDevice() =
        Build.MANUFACTURER.equals("samsung", ignoreCase = true) && isBetweenAndroidVersions(
            Build.VERSION_CODES.LOLLIPOP,
            Build.VERSION_CODES.LOLLIPOP_MR1
        )

    private fun isBetweenAndroidVersions(min: Int, max: Int) = Build.VERSION.SDK_INT in min..max

    private fun getContextForDateTimePicker(context: Context) : Context {
        if (isBrokenSamsungDevice())
            return object : ContextWrapper(context) {
                private var wrappedResources: Resources? = null

                override fun getResources(): Resources? {
                    val r = super.getResources()
                    if (wrappedResources == null) {
                        wrappedResources = object : Resources(r.assets, r.displayMetrics, r.configuration) {
                            @NonNull
                            @Throws(NotFoundException::class)
                            override fun getString(id: Int, vararg formatArgs: Any): String {
                                try {
                                    return super.getString(id, formatArgs)
                                } catch (ifce: IllegalFormatConversionException) {
                                    var template = super.getString(id)
                                    template = template.replace(("%" + ifce.conversion).toRegex(), "%s")
                                    return String.format(configuration.locale, template, formatArgs)
                                }
                            }
                        }
                    }
                    return wrappedResources
                }
            }
        return context
    }

    fun showDatePickerDialog(context: Context, selectedDate: Calendar, current: Calendar, handler: DateTimeHandler) {
        val calendar: Calendar = selectedDate
        val ctx = getContextForDateTimePicker(context)
        val boundDatePickerDialog = BoundDatePickerDialog(ctx,
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
                        ctx,
                        calendar,
                        -1,
                        24,
                        calendar.get(Calendar.HOUR_OF_DAY),
                        calendar.get(Calendar.MINUTE),
                        handler
                    )

                    calendar.time.after(current.time) ->
                        showTimePickerDialog(
                            ctx,
                            calendar,
                            current.get(Calendar.HOUR_OF_DAY),
                            current.get(Calendar.MINUTE),
                            calendar.get(Calendar.HOUR_OF_DAY),
                            calendar.get(Calendar.MINUTE),
                            handler
                        )

                    else -> showTimePickerDialog(
                        ctx,
                        calendar,
                        current.get(Calendar.HOUR_OF_DAY),
                        current.get(Calendar.MINUTE),
                        current.get(Calendar.HOUR_OF_DAY),
                        current.get(Calendar.MINUTE),
                        handler
                    )
                }
            })

        boundDatePickerDialog.datePicker.minDate = current.timeInMillis
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
        val ctx = getContextForDateTimePicker(context)
        BoundTimePickerDialog(
            ctx,
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
