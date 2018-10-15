package com.kg.gettransfer.presentation.ui

import com.kg.gettransfer.R

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_image.*

class FragmentImageForViewPager : Fragment() {

    companion object {

        val EXTRA_imageUrl = "imageUrl"

        fun newInstance(imageUrl: String): FragmentImageForViewPager {
            val fragmentImageForViewPager = FragmentImageForViewPager()
            val args = Bundle()
            args.putString(EXTRA_imageUrl, imageUrl)
            fragmentImageForViewPager.arguments = args

            return fragmentImageForViewPager
        }
    }

    private var imageUrl: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        imageUrl = arguments!!.getString(EXTRA_imageUrl)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_image, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadImage()
    }

    private fun loadImage() {
        ImageUtil.loadImage(activity, imageUrl, imageForViewPager)
    }
}