package com.kg.gettransfer.presentation.ui

import com.kg.gettransfer.R
import android.os.Bundle
import android.support.annotation.CallSuper
import android.support.annotation.StringRes
import android.support.v4.view.ViewPager
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.arellomobile.mvp.MvpAppCompatFragment
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.kg.gettransfer.domain.ApiException
import com.kg.gettransfer.extensions.isVisible
import com.kg.gettransfer.presentation.adapter.CarrierTripsCalendarRVAdapter
import com.kg.gettransfer.presentation.adapter.CarrierTripsCalendarMonthPagerAdapter
import com.kg.gettransfer.presentation.model.CarrierTripBaseModel
import com.kg.gettransfer.presentation.presenter.CarrierTripsCalendarFragmentPresenter
import com.kg.gettransfer.presentation.view.BaseView
import com.kg.gettransfer.presentation.view.CarrierTripsCalendarFragmentView
import kotlinx.android.synthetic.main.activity_carrier_trips_calendar_fragment.*
import timber.log.Timber

class CarrierTripsCalendarFragment : MvpAppCompatFragment(), CarrierTripsCalendarFragmentView{
    @InjectPresenter
    internal lateinit var presenter: CarrierTripsCalendarFragmentPresenter

    var currentIndex: Int = 1

    @ProvidePresenter
    fun createCarrierTripsCalendarPresenter() = CarrierTripsCalendarFragmentPresenter()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) =
            inflater.inflate(R.layout.activity_carrier_trips_calendar_fragment, container, false)

    @CallSuper
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewPager.adapter = CarrierTripsCalendarMonthPagerAdapter(childFragmentManager)
        viewPager.setCurrentItem(1, false)
        setPageChangeListener()
        rvCarrierTrips.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
    }

    override fun setCalendarIndicators(calendarItems: Map<String, List<CarrierTripBaseModel>>){
        setDataInCalendarFragments(calendarItems)
        setPageChangeListener(calendarItems)
    }

    private fun setPageChangeListener(calendarItems: Map<String, List<CarrierTripBaseModel>>? = null){
        viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {

            override fun onPageSelected(position: Int) {
                currentIndex = position
            }

            override fun onPageScrolled(arg0: Int, arg1: Float, arg2: Int) {}

            override fun onPageScrollStateChanged(state: Int) {
                if (state == ViewPager.SCROLL_STATE_IDLE) {
                    setDataInCalendarFragments(calendarItems)
                }
            }
        })
    }

    private fun setDataInCalendarFragments(calendarItems: Map<String, List<CarrierTripBaseModel>>? = null){
        for (i in 0 until CarrierTripsCalendarMonthPagerAdapter.MONTHS_COUNT) {
            val fragment = (viewPager.adapter as CarrierTripsCalendarMonthPagerAdapter).instantiateItem(viewPager, i) as CarrierTripsCalendarMonthFragment
            fragment.setUpCalendarAdapter(calendarItems, presenter.selectedDate, currentIndex - 1) { date -> presenter.onDateClick(date)}
        }
        viewPager.setCurrentItem(1, false)
    }

    private fun selectDate(selectedDate: String){
        for (i in 0 until CarrierTripsCalendarMonthPagerAdapter.MONTHS_COUNT) {
            val fragment = (viewPager.adapter as CarrierTripsCalendarMonthPagerAdapter).instantiateItem(viewPager, i) as CarrierTripsCalendarMonthFragment
            fragment.selectDate(selectedDate)
        }
    }

    override fun setItemsInRVDailyTrips(items: List<CarrierTripBaseModel>, selectedDate: String) {
        rvCarrierTrips.adapter = CarrierTripsCalendarRVAdapter(presenter, items)
        textNoTrips.isVisible = items.isEmpty()
        selectDate(selectedDate)
    }

    override fun blockInterface(block: Boolean, useSpinner: Boolean) =
            (activity as BaseView).blockInterface(block, useSpinner)

    override fun setError(finish: Boolean, @StringRes errId: Int, vararg args: String?) =
            (activity as BaseView).setError(finish, errId, *args)

    override fun setError(e: ApiException) {
        Timber.e("code: ${e.code}", e)
        Utils.showError(context!!, false, getString(R.string.err_server, e.message))
    }
}