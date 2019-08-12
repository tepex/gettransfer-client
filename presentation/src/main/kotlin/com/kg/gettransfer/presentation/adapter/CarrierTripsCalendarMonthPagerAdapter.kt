package com.kg.gettransfer.presentation.adapter

import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.kg.gettransfer.presentation.ui.CarrierTripsCalendarMonthFragment

class CarrierTripsCalendarMonthPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {
    companion object {
        const val MONTHS_COUNT = 3
    }

    override fun getCount() = MONTHS_COUNT
    override fun getItem(position: Int) = CarrierTripsCalendarMonthFragment.newInstance(position)

}