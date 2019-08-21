package com.kg.gettransfer.presentation.ui

import android.content.Context
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import android.widget.LinearLayout
import android.widget.TextView

import androidx.annotation.CallSuper
import androidx.core.widget.TextViewCompat
import androidx.fragment.app.Fragment

import com.kg.gettransfer.R
import com.kg.gettransfer.presentation.adapter.CarrierTripsCalendarGridAdapter
import com.kg.gettransfer.presentation.adapter.ClickOnDateHandler
import com.kg.gettransfer.presentation.model.CarrierTripBaseModel
import com.kg.gettransfer.presentation.ui.days.GTDayOfWeek

import java.util.Calendar
import java.util.Date

import kotlinx.android.synthetic.main.carrier_trips_calendar_month_fragment.*
//import leakcanary.AppWatcher

class CarrierTripsCalendarMonthFragment : Fragment() {

    private var monthIndex: Int = 0
    private var firstDayOfWeek: Int = 1
    private val cal = Calendar.getInstance()
    private lateinit var adapterCarrierTripsCalendar: CarrierTripsCalendarGridAdapter

    @CallSuper
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        monthIndex = arguments!!.getInt(EXTRA_MONTH_INDEX)
        firstDayOfWeek = arguments!!.getInt(EXTRA_FIRST_DAY_OF_WEEK)
        cal.add(Calendar.MONTH, monthIndex - 1)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) =
        inflater.inflate(R.layout.carrier_trips_calendar_month_fragment, container, false)

    @CallSuper
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpCalendarAdapter(
            view.context,
            null,
            SystemUtils.formatDateWithoutTime(Calendar.getInstance().time),
            0,
            null
        )
    }

    fun setUpCalendarAdapter(
        context: Context,
        calendarItems: Map<String, List<CarrierTripBaseModel>>?,
        selectedDate: String,
        itemIndex: Int,
        listener: ClickOnDateHandler?
    ) {
        cal.add(Calendar.MONTH, itemIndex)
        val dayValueInCells = ArrayList<Date>()
        val mCal = cal.clone() as Calendar
        mCal.set(Calendar.DAY_OF_MONTH, 1)
        val firstDayOfTheMonth = mCal.get(Calendar.DAY_OF_WEEK) + getOffset()
        mCal.add(Calendar.DAY_OF_MONTH, -firstDayOfTheMonth)
        var i = 0
        if (layoutDaysOfWeek != null && layoutDaysOfWeek.childCount > 0) layoutDaysOfWeek.removeAllViews()
        while (dayValueInCells.size < MAX_CALENDAR_COLUMN) {
            if (i < 7) {
                val textViewDayOfWeek = TextView(context)
                textViewDayOfWeek.text = SystemUtils.formatShortNameDayOfWeek(mCal.time).toUpperCase()
                textViewDayOfWeek.gravity = Gravity.CENTER
                TextViewCompat.setTextAppearance(textViewDayOfWeek, R.style.calendar_item_days_of_week)
                layoutDaysOfWeek?.addView(textViewDayOfWeek)
                textViewDayOfWeek.layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    1f
                )
                i++
            }
            dayValueInCells.add(mCal.time)
            mCal.add(Calendar.DAY_OF_MONTH, 1)
        }

        val monthYear = SystemUtils.formatMonthYear(cal.time)
        monthAndYear?.text = monthYear.substring(0, 1).toUpperCase().plus(monthYear.substring(1))
        adapterCarrierTripsCalendar =
            CarrierTripsCalendarGridAdapter(context, dayValueInCells, selectedDate, cal, calendarItems, listener)
        gridViewCalendar?.adapter = adapterCarrierTripsCalendar
    }

    private fun getOffset() =
        (GTDayOfWeek.getWeekDays().find { it.day == firstDayOfWeek } ?: GTDayOfWeek.getWeekDays().first()).offset

    fun selectDate(selectedDate: String) {
        adapterCarrierTripsCalendar.selectDate(selectedDate)
    }

    override fun onDestroy() {
        super.onDestroy()
//        AppWatcher.objectWatcher.watch(this)
    }

    companion object {
        const val EXTRA_MONTH_INDEX = "monthIndex"
        const val EXTRA_FIRST_DAY_OF_WEEK = "firstDayOfWeek"
        const val MAX_CALENDAR_COLUMN = 42

        fun newInstance(monthIndex: Int, firstDayOfWeek: Int) = CarrierTripsCalendarMonthFragment().apply {
            arguments = Bundle().apply {
                putInt(EXTRA_MONTH_INDEX, monthIndex)
                putInt(EXTRA_FIRST_DAY_OF_WEEK, firstDayOfWeek)
            }
        }
    }
}
