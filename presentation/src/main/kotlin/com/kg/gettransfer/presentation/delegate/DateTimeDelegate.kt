package com.kg.gettransfer.presentation.delegate

import android.content.Context

import com.kg.gettransfer.domain.interactor.OrderInteractor
import com.kg.gettransfer.domain.interactor.SessionInteractor

import com.kg.gettransfer.extensions.simpleFormat

import com.kg.gettransfer.presentation.ui.helpers.DateTimeHandler
import com.kg.gettransfer.presentation.ui.helpers.DateTimePickerHelper
import com.kg.gettransfer.presentation.ui.helpers.DateTimeScreen

import org.koin.core.KoinComponent
import org.koin.core.inject

import java.util.Calendar
import java.util.Date

class DateTimeDelegate : KoinComponent {

    val orderInteractor: OrderInteractor by inject()
    val sessionInteractor: SessionInteractor by inject()

    lateinit var currentData: Calendar

    var startDate: Date = Date()
        set(value) {
            field = value
            orderInteractor.orderStartTime = value
        }
    var returnDate: Date? = null
        set(value) {
            field = value
            orderInteractor.orderReturnTime = value
        }

    val startOrderedTime
        get() = orderInteractor.orderStartTime?.simpleFormat()
    val returnOrderedTime
        get() = orderInteractor.orderReturnTime?.simpleFormat()

    /*fun validateWith(errorAction: (Boolean) -> Unit) = compareDates().also { if (!it) errorAction(it) }

    fun validate() = validateWith { }*/

    fun validate() = compareDates()

    // check exactly what is in domain
    private fun compareDates() = orderInteractor.run {
            // true if hourly or return date not defined
            if (hourlyDuration != null) true else orderReturnTime?.after(orderStartTime) ?: true
        }

    fun chooseOrderTime(context: Context, fieldStart: Boolean, screen: DateTimeScreen?) =
        DateTimePickerHelper.showDatePickerDialog(
            context,
            Calendar.getInstance().apply { time = if (fieldStart) startDate else returnDate ?: startDate },
            getCurrentDateForField(fieldStart),
            object : DateTimeHandler {

                override fun onDateChosen(date: Date) {
                    handleDateChoice(date, fieldStart)
                }

                override fun onTimeChosen(date: Date) {
                    handleTimeChoice(date, fieldStart).also {
                        screen?.setFieldDate(date.simpleFormat(), fieldStart)
                    }
                }
            }
        )

    private fun getCurrentDateForField(startsField: Boolean): Calendar {
        currentData = getCurrentDate()
        if (!startsField) {
            currentData.time = Date(startDate.time + DATE_OFFSET)
        }
        return currentData
    }

    fun getCurrentDate(): Calendar = Calendar.getInstance(sessionInteractor.locale)

    private fun handleDateChoice(date: Date, field: Boolean) {
        if (field == START_DATE) startDate = date else returnDate = date
    }

    private fun handleTimeChoice(date: Date, startField: Boolean): Date =
        getCurrentDate().run {
            (if (date.after(time)) date else time).also { if (startField) startDate = it else returnDate = it }
        }

    fun resetAfterOrder() {
        currentData.time = startDate
        returnDate = null
    }

    companion object {
        const val START_DATE = true
        private const val DATE_OFFSET = 1000 * 60 * 5 // 5 minutes
    }
}
