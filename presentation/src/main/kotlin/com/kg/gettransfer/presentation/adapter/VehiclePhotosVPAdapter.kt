package com.kg.gettransfer.presentation.adapter

import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter

import com.kg.gettransfer.presentation.ui.FragmentImageForViewPager

class VehiclePhotosVPAdapter(fm: FragmentManager, private val urls: List<String>): FragmentStatePagerAdapter(fm) {
    private val fragments = Array<FragmentImageForViewPager?>(urls.size) { null }

    override fun getCount() = fragments.size    
    override fun getItem(pos: Int) = fragments[pos] ?: FragmentImageForViewPager.newInstance(urls[pos]).also { fragments[pos] = it } 
}
