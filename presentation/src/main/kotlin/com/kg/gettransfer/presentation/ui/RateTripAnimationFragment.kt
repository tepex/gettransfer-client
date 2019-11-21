package com.kg.gettransfer.presentation.ui

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.fragment.app.Fragment
import androidx.vectordrawable.graphics.drawable.Animatable2Compat
import androidx.vectordrawable.graphics.drawable.AnimatedVectorDrawableCompat

import com.kg.gettransfer.R
import com.kg.gettransfer.extensions.isVisible

import kotlinx.android.synthetic.main.fragment_rate_trip_animation.*

class RateTripAnimationFragment : Fragment(R.layout.fragment_rate_trip_animation) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        startAnimation()
    }

    private fun startAnimation() {
        val animatedVector = AnimatedVectorDrawableCompat.create(requireContext(), R.drawable.avd_progress_circular)
        ivProgress.setImageDrawable(animatedVector)
        fadeIn()
        animatedVector?.apply {
            registerAnimationCallback(object : Animatable2Compat.AnimationCallback() {
                override fun onAnimationEnd(drawable: Drawable?) {
                    super.onAnimationEnd(drawable)
                    fadeOut()
                }
            })
            start()
        }
    }

    private fun fadeIn() {
        ivCircle.startAnimation(AnimationUtils.loadAnimation(requireContext(), android.R.anim.fade_in))
    }

    private fun fadeOut() {
        val animation = AnimationUtils.loadAnimation(requireContext(), android.R.anim.fade_out)
        animation.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationRepeat(animation: Animation?) {}

            override fun onAnimationEnd(animation: Animation?) {
                hideProgress()
            }

            override fun onAnimationStart(animation: Animation?) {}
        })
        ivProgress.startAnimation(animation)
    }

    private fun hideProgress() {
        ivCircle.isVisible = false
        ivProgress.isVisible = false
        groupComplete.isVisible = true
        Handler().postDelayed({ closeFragment() }, DELAY_TO_CLOSE_FRAGMENT)
    }

    private fun closeFragment() {
        if (isAdded) {
            fragmentManager?.beginTransaction()?.apply {
                setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                remove(this@RateTripAnimationFragment)
                commit()
            }
        }
    }

    companion object {
        private const val DELAY_TO_CLOSE_FRAGMENT = 800L
    }
}
