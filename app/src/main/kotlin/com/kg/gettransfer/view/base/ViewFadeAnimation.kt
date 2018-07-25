package com.kg.gettransfer.view.base

import android.view.View
import android.view.animation.AlphaAnimation

/**
 * Created by denisvakulenko on 22/03/2018.
 */

val fadeInAnimation = object : AlphaAnimation(0f, 1f) {
    init {
        duration = 250
        fillAfter
    }
}

fun View.fadeIn() {
    if (visibility == View.VISIBLE) return
    visibility = View.VISIBLE
    startAnimation(fadeInAnimation)
}

fun View.hide(gone: Boolean = false) {
    visibility = if (gone) View.GONE else View.INVISIBLE
    clearAnimation()
}