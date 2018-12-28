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
import android.support.v4.widget.TextViewCompat
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
        const val TRIPS_INDICATOR_WIDTH_4DP = 4f
        const val TRIPS_INDICATOR_WIDTH_4DP_MIN_COUNT = 1
        const val TRIPS_INDICATOR_WIDTH_4DP_MAX_COUNT = 4

        const val TRIPS_INDICATOR_WIDTH_8DP = 8f
        const val TRIPS_INDICATOR_WIDTH_8DP_MIN_COUNT = 5
        const val TRIPS_INDICATOR_WIDTH_8DP_MAX_COUNT = 9

        const val TRIPS_INDICATOR_WIDTH_16DP = 16f
        const val TRIPS_INDICATOR_WIDTH_16DP_MIN_COUNT = 10
    }

    private val mInflater: LayoutInflater = LayoutInflater.from(context)

    private val itemsViews = hashMapOf<String, View>()

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

        if (displayMonth == currentMonth && displayYear == currentYear) {
            TextViewCompat.setTextAppearance(view!!.dayOfMonth, R.style.calendar_item_current_month_days)
        } else {
            TextViewCompat.setTextAppearance(view!!.dayOfMonth, R.style.calendar_item_not_current_month_days)
        }

        if(DateUtils.isToday(mDate.time)) view.dayOfMonth.setTextColor(ContextCompat.getColor(context, R.color.color_driver_mode_text_green))
        if(dateString == selectedDate) view.selectDateIndicator.isVisible = true
        val size = calendarItems?.get(dateString)?.size?: 0
        when{
            size in TRIPS_INDICATOR_WIDTH_4DP_MIN_COUNT..TRIPS_INDICATOR_WIDTH_4DP_MAX_COUNT -> { setIndicatorWidth(TRIPS_INDICATOR_WIDTH_4DP, view.countTripsIndicator) }
            size in TRIPS_INDICATOR_WIDTH_8DP_MIN_COUNT..TRIPS_INDICATOR_WIDTH_8DP_MAX_COUNT -> { setIndicatorWidth(TRIPS_INDICATOR_WIDTH_8DP, view.countTripsIndicator) }
            size >= TRIPS_INDICATOR_WIDTH_16DP_MIN_COUNT -> { setIndicatorWidth(TRIPS_INDICATOR_WIDTH_16DP, view.countTripsIndicator) }
        }
        view.dayOfMonth.text = dayValue.toString()
        view.setOnClickListener { listener?.invoke(dateString) }
        itemsViews[dateString] = view
        return view
    }

    private fun setIndicatorWidth(dp: Float, indicatorView: View){
        val lp = indicatorView.layoutParams
        lp.width = Utils.convertDpToPixels(context, dp).toInt()
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
        itemsViews.values.toMutableList().map { it.selectDateIndicator.isVisible = false }
        itemsViews[selectedDate]?.let { it.selectDateIndicator.isVisible = true }
    }
}

typealias ClickOnDateHandler = (String) -> Unit