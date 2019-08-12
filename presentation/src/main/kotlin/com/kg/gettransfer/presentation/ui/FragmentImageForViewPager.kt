package com.kg.gettransfer.presentation.ui

import android.os.Bundle

import androidx.fragment.app.Fragment

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.bumptech.glide.Glide

import com.kg.gettransfer.R

import kotlinx.android.synthetic.main.fragment_image.*

class FragmentImageForViewPager: Fragment() {
    companion object {
        const val EXTRA_IMAGE_URL = "imageUrl"

        fun newInstance(imageUrl: String) = FragmentImageForViewPager().apply {
            arguments = Bundle().apply { putString(EXTRA_IMAGE_URL, imageUrl) }
        }
    }

    private lateinit var imageUrl: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        imageUrl = arguments!!.getString(EXTRA_IMAGE_URL)!!
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) =
        inflater.inflate(R.layout.fragment_image, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Glide.with(activity!!).load(imageUrl).into(imageForViewPager)
    }
}
