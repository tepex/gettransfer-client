package com.kg.gettransfer.presentation.ui

import android.content.Context
import android.support.v4.app.Fragment
import com.kg.gettransfer.R
import android.view.ViewGroup
import android.os.Bundle
import android.support.v4.widget.TextViewCompat
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import com.kg.gettransfer.domain.interactor.SystemInteractor
import com.kg.gettransfer.presentation.adapter.CarrierTripsCalendarGridAdapter
import com.kg.gettransfer.presentation.adapter.ClickOnDateHandler
import com.kg.gettransfer.presentation.model.CarrierTripBaseModel
import com.kg.gettransfer.presentation.ui.days.GTDayOfWeek
import io.sentry.Sentry
import kotlinx.android.synthetic.main.carrier_trips_calendar_month_fragment.*
import org.koin.android.ext.android.inject
import java.util.Calendar
import java.util.Date

class CarrierTripsCalendarMonthFragment : Fragment() {

    companion object {
        const val EXTRA_MONTH_INDEX = "monthIndex"
        const val MAX_CALENDAR_COLUMN = 42

        fun newInstance(monthIndex: Int) = CarrierTripsCalendarMonthFragment().apply {
            arguments = Bundle().apply { putInt(EXTRA_MONTH_INDEX, monthIndex) }
        }
    }

    private var monthIndex: Int = 0

    private var mAdapterCarrierTripsCalendar: CarrierTripsCalendarGridAdapter? = null
    private val cal = Calendar.getInstance()
    private val systemInteractor: SystemInteractor by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        monthIndex = arguments!!.getInt(EXTRA_MONTH_INDEX)
        cal.add(Calendar.MONTH, monthIndex - 1)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) =
        inflater.inflate(R.layout.carrier_trips_calendar_month_fragment, container, false)

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
        mAdapterCarrierTripsCalendar =
            CarrierTripsCalendarGridAdapter(context, dayValueInCells, selectedDate, cal, calendarItems, listener)
        gridViewCalendar?.adapter = mAdapterCarrierTripsCalendar
    }

    private fun getOffset() =
        (GTDayOfWeek.getWeekDays().find { it.day == systemInteractor.firstDayOfWeek }
            ?: GTDayOfWeek.getWeekDays().first()).offset

    fun selectDate(selectedDate: String) {
        mAdapterCarrierTripsCalendar?.selectDate(selectedDate)
    }
}