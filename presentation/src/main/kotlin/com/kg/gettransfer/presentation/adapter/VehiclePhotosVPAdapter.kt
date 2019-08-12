package com.kg.gettransfer.presentation.adapter

import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter

import com.kg.gettransfer.presentation.ui.FragmentImageForViewPager
//TODO not used
class VehiclePhotosVPAdapter(
    fm: FragmentManager,
    private val urls: List<String>
) : FragmentStatePagerAdapter(fm) {

    private val fragments = Array<FragmentImageForViewPager?>(urls.size) { null }

    override fun getCount() = fragments.size
    override fun getItem(pos: Int) = fragments[pos] ?: FragmentImageForViewPager.newInstance(urls[pos]).also { fragments[pos] = it }
}
