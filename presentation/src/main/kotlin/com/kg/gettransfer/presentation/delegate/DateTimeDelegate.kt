package com.kg.gettransfer.presentation.delegate

import android.content.Context

import com.kg.gettransfer.R
import com.kg.gettransfer.core.presentation.WorkerManager
import com.kg.gettransfer.domain.interactor.OrderInteractor
import com.kg.gettransfer.domain.interactor.SessionInteractor

import com.kg.gettransfer.extensions.simpleFormat

import com.kg.gettransfer.presentation.ui.helpers.DateTimeHandler
import com.kg.gettransfer.presentation.ui.helpers.DateTimePickerHelper
import com.kg.gettransfer.presentation.ui.helpers.DateTimeScreen

import com.kg.gettransfer.sys.presentation.ConfigsManager
import kotlinx.coroutines.launch

import org.koin.core.KoinComponent
import org.koin.core.inject
import org.koin.core.parameter.parametersOf

import java.util.Calendar
import java.util.Date

class DateTimeDelegate : KoinComponent {

    val orderInteractor: OrderInteractor by inject()
    val sessionInteractor: SessionInteractor by inject()
    val configsManager: ConfigsManager by inject()
    private val worker: WorkerManager by inject { parametersOf("DateTimeDelegate") }

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
    private fun compareDates() =
        orderInteractor.run {
                                                                          // true if hourly or return date not defined
            if (hourlyDuration != null) true else orderReturnTime?.after(orderStartTime) ?: true
        }

    fun chooseOrderTime(context: Context, fieldStart: Boolean, screen: DateTimeScreen?) = worker.main.launch {
        DateTimePickerHelper.showDatePickerDialog(
            context,
            Calendar.getInstance().apply { time = if (fieldStart) startDate else returnDate ?: startDate},
            getCurrentDateForField(fieldStart),
            object : DateTimeHandler {

                override fun onDateChosen(date: Date) {
                    handleDateChoice(date, fieldStart)
                }

                override fun onTimeChosen(date: Date) {
                    worker.main.launch {
                        handleTimeChoice(date, fieldStart).also {
                            screen?.setFieldDate(getDisplayText(date, context), fieldStart)
                        }
                    }
                }
            }
        )
    }

    private suspend fun getCurrentDateForField(startsField: Boolean): Calendar {
        currentData = getCurrentDatePlusMinimumHours()
        if (!startsField) {
            currentData.time = Date(startDate.time + DATE_OFFSET)
        }
        return currentData
    }

    suspend fun getCurrentDatePlusMinimumHours(): Calendar {
        val calendar = Calendar.getInstance(sessionInteractor.locale)
        /* Server must send current locale time */
        calendar.add(Calendar.HOUR_OF_DAY, configsManager.getMobileConfigs().orderMinimum.hours.hours)
        calendar.add(Calendar.MINUTE, FUTURE_MINUTE)
        return calendar
    }

    private fun handleDateChoice(date: Date, field: Boolean) {
        if (field == START_DATE) startDate = date else returnDate = date
    }

    private suspend fun handleTimeChoice(date: Date, startField: Boolean): Date =
        getCurrentDatePlusMinimumHours().run {
            (if (date.after(time)) date else time).also { if (startField) startDate = it else returnDate = it }
        }

    private suspend fun getDisplayText(date: Date, context: Context) =
        if (date.after(getCurrentDatePlusMinimumHours().time)) date.simpleFormat() else getTextForMinDate(context)

    private suspend fun getTextForMinDate(context: Context) = context.getString(R.string.LNG_DATE_IN_HOURS)
        .plus(" ")
        .plus(configsManager.getMobileConfigs().orderMinimum.hours.hours)
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
