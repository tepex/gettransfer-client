package com.kg.gettransfer.presentation.delegate

import android.content.Context
import com.kg.gettransfer.R
import com.kg.gettransfer.domain.interactor.OrderInteractor
import com.kg.gettransfer.domain.interactor.SessionInteractor
import com.kg.gettransfer.domain.interactor.SystemInteractor
import com.kg.gettransfer.extensions.simpleFormat
import com.kg.gettransfer.presentation.ui.helpers.DateTimeHandler
import com.kg.gettransfer.presentation.ui.helpers.DateTimePickerHelper
import com.kg.gettransfer.presentation.ui.helpers.DateTimeScreen
import org.koin.standalone.KoinComponent
import org.koin.standalone.get
import java.util.*

class DateTimeDelegate : KoinComponent {
    val systemInteractor: SystemInteractor = get()
    val orderInteractor: OrderInteractor = get()
    val sessionInteractor: SessionInteractor = get()

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
    private val futureHour
        get() = sessionInteractor.mobileConfigs.orderMinimumMinutes / 60
    val startOrderedTime
        get() = orderInteractor.orderStartTime?.simpleFormat()
    val returnOrderedTime
        get() = orderInteractor.orderReturnTime?.simpleFormat()

    fun validateWith(errorAction: (Boolean) -> Unit) =
        compareDates()
            .also { if (!it) errorAction(it) }

    fun validate() =
        validateWith { }

    private fun compareDates() =                                          //check exactly what is in domain
        orderInteractor.run {
            if (hourlyDuration != null) true
            else orderReturnTime?.after(orderStartTime) ?: true  //true if hourly or return date not defined
        }

    fun chooseOrderTime(context: Context, fieldStart: Boolean, screen: DateTimeScreen?) =
        DateTimePickerHelper.showDatePickerDialog(
            context,
            getCurrentDateForField(fieldStart),
            object : DateTimeHandler {
                override fun onDateChosen(date: Date) {
                    handleDateChoice(date, fieldStart)
                }

                override fun onTimeChosen(date: Date) {
                    handleTimeChoice(date, fieldStart).also {
                        screen?.setFieldDate(getDisplayText(date, context), fieldStart)
                    }
                }
            })

    private fun getCurrentDateForField(startsField: Boolean): Calendar {
        if (startsField) currentData = getCurrentDatePlusMinimumHours()
        else currentData.time = Date(startDate.time + DATE_OFFSET)
        return currentData
    }

    fun getCurrentDatePlusMinimumHours(): Calendar {
        val calendar = Calendar.getInstance(sessionInteractor.locale)
        /* Server must send current locale time */
        calendar.add(Calendar.HOUR_OF_DAY, futureHour)
        calendar.add(Calendar.MINUTE, FUTURE_MINUTE)
        return calendar
    }

    private fun handleDateChoice(date: Date, field: Boolean) {
        if (field == START_DATE) startDate = date
        else returnDate = date
    }

    private fun handleTimeChoice(date: Date, startField: Boolean): Date =
        getCurrentDatePlusMinimumHours().run {
            (if (date.after(time)) date else time)
                .also {
                    if (startField) startDate = it
                    else returnDate = it
                }
        }

    private fun getDisplayText(date: Date, context: Context) =
        if (date.after(getCurrentDatePlusMinimumHours().time)) date.simpleFormat()
        else getTextForMinDate(context)

    private fun getTextForMinDate(context: Context) = context.getString(R.string.LNG_DATE_IN_HOURS)
        .plus(" ")
        .plus(futureHour)
        .plus(" ")
        .plus(context.getString(R.string.LNG_HOUR_FEW))

    fun resetAfterOrder() {
        currentData.time = startDate
        returnDate = null
    }

    companion object {
        /* Пока сервевер не присылает минимальный временной промежуток до заказа */
        private const val FUTURE_MINUTE = 5
        const val START_DATE = true
        const val RETURN_DATE = false
        private const val DATE_OFFSET = 1000 * 60 * 5 // 5 minutes
    }
}

