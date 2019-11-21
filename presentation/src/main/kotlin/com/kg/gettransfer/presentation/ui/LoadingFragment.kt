package com.kg.gettransfer.presentation.ui

import android.os.Bundle

import androidx.annotation.CallSuper

import androidx.fragment.app.Fragment

import android.view.View

import android.view.animation.Animation
import android.view.animation.RotateAnimation

import com.kg.gettransfer.R

import kotlinx.android.synthetic.main.fragment_loading_view.*

// import leakcanary.AppWatcher

class LoadingFragment : Fragment(R.layout.fragment_loading_view) {

    @CallSuper
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val anim = RotateAnimation(
            FROM_DEGREES,
            TO_DEGREES,
            Animation.RELATIVE_TO_SELF,
            PIVOT_X_VALUE,
            Animation.RELATIVE_TO_SELF,
            PIVOT_Y_VALUE)

        anim.duration = DURATION
        anim.repeatCount = Animation.INFINITE
        spinner.startAnimation(anim)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        spinner.clearAnimation()
    }

    override fun onDestroy() {
        super.onDestroy()
//        AppWatcher.objectWatcher.watch(this)
    }

    companion object {
        private const val FROM_DEGREES = 0f
        private const val TO_DEGREES = 360f
        private const val PIVOT_X_VALUE = 0.5f
        private const val PIVOT_Y_VALUE = 0.5f
        private const val DURATION = 1500L
    }
}
