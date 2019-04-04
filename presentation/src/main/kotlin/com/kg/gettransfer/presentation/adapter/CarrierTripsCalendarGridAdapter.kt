package com.kg.gettransfer.presentation.adapter

import com.kg.gettransfer.R
import android.content.Context
import android.support.v4.content.ContextCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import kotlinx.android.synthetic.main.view_carrier_trips_calendar_item.view.*
import java.util.Calendar
import java.util.Date
import android.text.format.DateUtils
import com.kg.gettransfer.extensions.isVisible
import com.kg.gettransfer.presentation.model.CarrierTripBaseModel
import com.kg.gettransfer.presentation.ui.SystemUtils
import com.kg.gettransfer.presentation.ui.Utils

class CarrierTripsCalendarGridAdapter(context: Context,
                                      private val monthlyDates: List<Date>,
                                      private val selectedDate: String,
                                      private val currentDate: Calendar,
                                      private val calendarItems: Map<String, List<CarrierTripBaseModel>>?,
                                      private val listener: ClickOnDateHandler?) : ArrayAdapter<Int>(context, R.layout.view_carrier_trips_calendar_item){
    companion object {
        const val TRIPS_SMALL_INDICATOR_SIZE = 4f
        //const val TRIPS_SMALL_INDICATOR_MIN_COUNT = 1
        //const val TRIPS_SMALL_INDICATOR_MAX_COUNT = 4
        const val TRIPS_SMALL_INDICATOR_MIN_MINUTES = 1 // >= 1 minute
        const val TRIPS_SMALL_INDICATOR_MAX_MINUTES = 4 * 60 - 1 // < 4 hours

        const val TRIPS_MEDIUM_INDICATOR_SIZE = 8f
        //const val TRIPS_MEDIUM_INDICATOR_MIN_COUNT = 5
        //const val TRIPS_MEDIUM_INDICATOR_MAX_COUNT = 9
        const val TRIPS_MEDIUM_INDICATOR_MIN_MINUTES = 4 // >= 4 hours
        const val TRIPS_MEDIUM_INDICATOR_MAX_MINUTES = 8 * 60 - 1 // < 8 hours

        const val TRIPS_LARGE_INDICATOR_SIZE = 16f
        //const val TRIPS_LARGE_INDICATOR_MIN_COUNT = 10
        const val TRIPS_LARGE_INDICATOR_MIN_MINUTES = 8 * 60 // >= 8 hours
    }

    private val mInflater: LayoutInflater = LayoutInflater.from(context)

    private val itemsViews = hashMapOf<String, MonthItem>() //Pair<dayInCurrentMonth: Boolean, view: View>

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val mDate = monthlyDates[position]
        val dateCal = Calendar.getInstance()
        dateCal.time = mDate
        val dateString = SystemUtils.formatDateWithoutTime(mDate)
        val dayValue = dateCal.get(Calendar.DAY_OF_MONTH)
        val displayMonth = dateCal.get(Calendar.MONTH) + 1
        val displayYear = dateCal.get(Calendar.YEAR)
        val currentMonth = currentDate.get(Calendar.MONTH) + 1
        val currentYear = currentDate.get(Calendar.YEAR)
        var view = convertView
        if (view == null) view = mInflater.inflate(R.layout.view_carrier_trips_calendar_item, parent, false)

        view!!
        //val size = calendarItems?.get(dateString)?.size?: 0
        val trips = calendarItems?.get(dateString)?: emptyList()
        var minutes = 0
        for (trip in trips){
            minutes += trip.time ?: trip.duration?.let { Utils.convertHoursToMinutes(it) } ?: 0
        }
        when{
            minutes in TRIPS_SMALL_INDICATOR_MIN_MINUTES..TRIPS_SMALL_INDICATOR_MAX_MINUTES -> { setIndicatorWidth(TRIPS_SMALL_INDICATOR_SIZE, view.countTripsIndicator) }
            minutes in TRIPS_MEDIUM_INDICATOR_MIN_MINUTES..TRIPS_MEDIUM_INDICATOR_MAX_MINUTES -> { setIndicatorWidth(TRIPS_MEDIUM_INDICATOR_SIZE, view.countTripsIndicator) }
            minutes >= TRIPS_LARGE_INDICATOR_MIN_MINUTES -> { setIndicatorWidth(TRIPS_LARGE_INDICATOR_SIZE, view.countTripsIndicator) }
        }
        /*when{
            size in TRIPS_SMALL_INDICATOR_MIN_COUNT..TRIPS_SMALL_INDICATOR_MAX_COUNT -> { setIndicatorWidth(TRIPS_SMALL_INDICATOR_SIZE, view.countTripsIndicator) }
            size in TRIPS_MEDIUM_INDICATOR_MIN_COUNT..TRIPS_MEDIUM_INDICATOR_MAX_COUNT -> { setIndicatorWidth(TRIPS_MEDIUM_INDICATOR_SIZE, view.countTripsIndicator) }
            size >= TRIPS_LARGE_INDICATOR_MIN_COUNT -> { setIndicatorWidth(TRIPS_LARGE_INDICATOR_SIZE, view.countTripsIndicator) }
        }*/
        view.dayOfMonth.text = dayValue.toString()
        view.setOnClickListener { listener?.invoke(dateString) }
        val monthItem = MonthItem(displayMonth == currentMonth && displayYear == currentYear, DateUtils.isToday(mDate.time), view)
        itemsViews[dateString] = monthItem
        changeItemView(monthItem, dateString == selectedDate)
        return view
    }

    private fun setIndicatorWidth(dp: Float, indicatorView: View){
        val lp = indicatorView.layoutParams
        lp.width = Utils.dpToPxInt(context, dp)
        indicatorView.layoutParams = lp
        indicatorView.isVisible = true
    }

    override fun getCount(): Int {
        return monthlyDates.size
    }

    override fun getItem(position: Int): Int? {
        val itemDate = Calendar.getInstance()
        itemDate.time = monthlyDates[position]
        return itemDate.get(Calendar.DAY_OF_MONTH)
    }

    fun selectDate(selectedDate: String){
        itemsViews.values.toMutableList().map { changeItemView(it, false) }
        itemsViews[selectedDate]?.let { changeItemView(it, true) }
    }

    private fun changeItemView(monthItem: MonthItem, isDateSelected: Boolean) {
        monthItem.view.selectDateIndicator.isVisible = isDateSelected
        monthItem.view.countTripsIndicator.isActivated = isDateSelected
        monthItem.view.dayOfMonth.setTextColor(ContextCompat.getColor(context, when {
            isDateSelected -> R.color.colorWhite
            monthItem.isToday -> R.color.color_gtr_orange
            monthItem.isDayInCurrentMonth -> R.color.colorTextBlack
            else -> R.color.color_driver_mode_text_gray
        }))
    }
}

class MonthItem(
        val isDayInCurrentMonth: Boolean,
        val isToday: Boolean,
        val view: View
)

typealias ClickOnDateHandler = (String) -> Unit