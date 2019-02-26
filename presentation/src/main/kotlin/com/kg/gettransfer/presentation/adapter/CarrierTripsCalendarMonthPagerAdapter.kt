package com.kg.gettransfer.presentation.adapter

import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import com.kg.gettransfer.presentation.ui.CarrierTripsCalendarMonthFragment

class CarrierTripsCalendarMonthPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {
    companion object {
        const val MONTHS_COUNT = 3
    }

    override fun getCount() = MONTHS_COUNT
    override fun getItem(position: Int) = CarrierTripsCalendarMonthFragment.newInstance(position)
}