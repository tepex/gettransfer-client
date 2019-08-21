package com.kg.gettransfer.extensions

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.view.View
import android.view.animation.AnimationUtils
import com.kg.gettransfer.R

fun View.visibleFade(value: Boolean, duration: Long = 200) {
    if (value) {
        alpha = 0f
        animate()
                .alpha(1f)
                .setDuration(duration)
                .setListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationStart(animation: Animator?) {
                        visibility = View.VISIBLE
                    }
                })
    } else {
        animate()
                .alpha(0f)
                .setDuration(duration)
                .setListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator) {
                        visibility = View.GONE
                    }
                })
    }
}

fun View.visibleSlideFade(value: Boolean, duration: Long = 100) {
    if (value) {
        if (isVisible) return
        alpha = 0f
        animate()
                .alpha(1f)
                .translationY(0f)
                .setDuration(duration)
                .setListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationStart(animation: Animator?) {
                        visibility = View.VISIBLE
                    }
                })
    } else {
        if (!isVisible) return
        this.animate()
                .alpha(0f)
                .translationY(height.toFloat())
                .setDuration(duration)
                .setListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator) {
                        visibility = View.GONE
                    }
                })
    }
}

fun View.visibleSlide(value: Boolean) {
    if (value) {
        if (isVisible) return
        visibility = View.VISIBLE
        startAnimation(AnimationUtils.loadAnimation(context, R.anim.transition_b2t))
    } else {
        if (!isVisible) return
        startAnimation(AnimationUtils.loadAnimation(context, R.anim.transition_t2b))
        visibility = View.GONE
    }
}

