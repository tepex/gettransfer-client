package com.kg.gettransfer.presentation.adapter

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import com.kg.gettransfer.presentation.ui.FragmentImageForViewPager
import java.util.ArrayList

class VehiclePhotosVPAdapter(fm: FragmentManager,
                             private val imagesUrls: List<String>): FragmentStatePagerAdapter(fm) {

    private val innerFragments: MutableList<FragmentImageForViewPager?> = ArrayList()

    override fun getItem(position: Int): Fragment {
        val innerFragment: FragmentImageForViewPager
        if (innerFragments.size > position) {
            innerFragment = innerFragments[position]!!
        } else {
            innerFragment = FragmentImageForViewPager.newInstance(imagesUrls[position])
            addToList(position, innerFragment)
        }
        return innerFragment
    }

    override fun getCount(): Int {
        return imagesUrls.size
    }

    private fun addToList(position: Int, innerFragment: FragmentImageForViewPager) {
        try {
            innerFragments[position] = innerFragment
        } catch (e: IndexOutOfBoundsException) {
            for (i in innerFragments.size until position + 1) {
                innerFragments.add(i, null)
            }
            innerFragments[position] = innerFragment
        }

    }
}