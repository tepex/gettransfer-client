package com.kg.gettransfer.presentation.ui

import android.os.Bundle

import android.support.annotation.CallSuper

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import android.view.animation.Animation
import android.view.animation.RotateAnimation

import com.kg.gettransfer.R

import kotlinx.android.synthetic.main.fragment_loading_view.*

class LoadingFragment: Fragment() {
    companion object {
        private val fragment by lazy { LoadingFragment() }
        
        fun showLoading(fragmentManager: FragmentManager) {
            if(fragment.isAdded) return
            fragmentManager.beginTransaction().apply {
                add(android.R.id.content, fragment)
                commit()
            }
        }

        fun hideLoading(fragmentManager: FragmentManager) =
            fragmentManager.beginTransaction().apply {
                remove(fragment)
                commit()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) =
        inflater.inflate(R.layout.fragment_loading_view, container, false)

    @CallSuper
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val anim = RotateAnimation(0f, 360f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f)
        anim.duration = 800
        anim.repeatCount = Animation.INFINITE
        spinner.startAnimation(anim)
    }    
}
