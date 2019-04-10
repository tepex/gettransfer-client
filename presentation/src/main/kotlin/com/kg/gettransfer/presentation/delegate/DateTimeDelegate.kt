package com.kg.gettransfer.presentation.delegate

import android.content.Context
import com.kg.gettransfer.R
import com.kg.gettransfer.domain.interactor.OrderInteractor
import com.kg.gettransfer.domain.interactor.SystemInteractor
import com.kg.gettransfer.extensions.simpleFormat
import com.kg.gettransfer.presentation.model.TripDate
import com.kg.gettransfer.presentation.ui.SystemUtils
import com.kg.gettransfer.presentation.ui.helpers.DateTimeHandler
import com.kg.gettransfer.presentation.ui.helpers.DateTimePickerHelper
import com.kg.gettransfer.presentation.ui.helpers.DateTimeScreen
import org.koin.standalone.KoinComponent
import org.koin.standalone.get
import java.util.*

class DateTimeDelegate: KoinComponent {
    val systemInteractor: SystemInteractor = get()
    val orderInteractor: OrderInteractor = get()

    private var currentData: Calendar
    var startDate: TripDate = TripDate(Date())
    var returnDate: TripDate? = null
    set(value) {
        field = value
        if (value == null) orderInteractor.orderReturnTime = null
    }
    private val futureHour
        get() = systemInteractor.mobileConfigs.orderMinimumMinutes / 60
    val startOrderedTime
        get() = orderInteractor.orderStartTime?.simpleFormat()
    val returnOrderedTime
        get() = orderInteractor.orderReturnTime?.simpleFormat()


    init {
        currentData = getCurrentDatePlusMinimumHours()
    }

     fun validateWith(errorAction: (Boolean) -> Unit) =
             compareDates()
                     .also { if (!it) errorAction(it) }

    fun validate() =
            validateWith {  }

    private fun compareDates() =
            orderInteractor.run {
                if (hourlyDuration != null) true
                else orderReturnTime?.after(orderStartTime) ?: true  //true if hourly or return date not defined
            }

    fun chooseOrderTime(context: Context, fieldStart: Boolean, screen: DateTimeScreen) =
        DateTimePickerHelper.showDatePickerDialog(context, currentData, object : DateTimeHandler {
            override fun onDateChosen(date: Date) { handleDateChoice(date, fieldStart) }
            override fun onTimeChosen(date: Date) {
                handleTimeChoice(date, fieldStart).also {
                        screen.setFieldDate(getDisplayText(date, context), fieldStart)
                }
            }
        })

    fun getCurrentDatePlusMinimumHours(): Calendar {
        val calendar = Calendar.getInstance(systemInteractor.locale)
        /* Server must send current locale time */
        calendar.add(Calendar.HOUR_OF_DAY, futureHour)
        calendar.add(Calendar.MINUTE, FUTURE_MINUTE)
        return calendar
    }

    private fun handleDateChoice(date: Date, field: Boolean) {
        if (field == START_DATE) startDate.date = date
        else returnDate = TripDate(date)
    }

    private fun handleTimeChoice(date: Date, field: Boolean): Date {
        if (field == RETURN_DATE && returnDate == null)
            returnDate = TripDate(startDate.date).apply { date.time += DATE_OFFSET }

        currentData = getCurrentDatePlusMinimumHours()
        val resultDate = if (date.after(currentData.time)) date else currentData.time
        with(orderInteractor) {
            if (field == START_DATE) {
                orderStartTime = resultDate
                currentData.time = startDate.date
            }
            else orderReturnTime = resultDate
        }
        return resultDate
    }

    private fun getDisplayText(date: Date, context: Context) =
            if (date.after(getCurrentDatePlusMinimumHours().time)) date.simpleFormat()
            else getTextForMinDate(context)

    fun getTextForMinDate(context: Context) = context.getString(R.string.LNG_DATE_IN_HOURS)
            .plus(" ")
            .plus(futureHour)
            .plus(" ")
            .plus(context.getString(R.string.LNG_HOUR_FEW))

    companion object {
        /* Пока сервевер не присылает минимальный временной промежуток до заказа */
        private const val FUTURE_MINUTE = 5
        const val START_DATE  = true
        const val RETURN_DATE = false
        private const val DATE_OFFSET = 1000 * 60 * 5 // 5 minutes
    }
}

