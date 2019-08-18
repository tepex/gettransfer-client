package com.kg.gettransfer.extensions

import android.app.Activity
import android.content.Context
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.view.inputmethod.InputMethodManager
import android.webkit.WebView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import androidx.transition.*
import com.kg.gettransfer.R
import com.kg.gettransfer.common.DebouncingOnClickListener

fun View.visibleSlide(value: Boolean, vg: ViewGroup, duration: Long = 300, startDelay: Long = 0) {
    val transition = Slide(if (value) Gravity.TOP else Gravity.BOTTOM)
            .addTarget(this)
            .setDuration(duration)
            .setInterpolator(FastOutSlowInInterpolator())
            .setStartDelay(startDelay)
    TransitionManager.beginDelayedTransition(vg, transition)
    visibility = if (value) View.VISIBLE else View.GONE
}

fun View.visibleFade(value: Boolean, vg: ViewGroup, duration: Long = 200, startDelay: Long = 100) {
    val transition = Fade()
            .addTarget(this)
            .setDuration(duration)
            .setInterpolator(FastOutSlowInInterpolator())
            .setStartDelay(startDelay)
    TransitionManager.beginDelayedTransition(vg, transition)
    visibility = if (value) View.VISIBLE else View.GONE
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

inline var View.isVisible: Boolean
    get() = visibility == View.VISIBLE
    set(value) {
        visibility = if (value) View.VISIBLE else View.GONE
    }

inline var View.isInvisible: Boolean
    get() = visibility == View.INVISIBLE
    set(value) {
        visibility = if (value) View.INVISIBLE else View.VISIBLE
    }

inline var View.isGone: Boolean
    get() = visibility == View.GONE
    set(value) {
        visibility = if (value) View.GONE else View.VISIBLE
    }

fun Fragment.hideKeyboard() {
    requireActivity().hideKeyboard(requireView())
}

fun AppCompatActivity.hideKeyboard() {
    hideKeyboard(if (currentFocus == null) View(this) else currentFocus)
}

fun Context.hideKeyboard(view: View) {
    val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
}

fun View.hideKeyboard() {
    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE)
    if (imm is InputMethodManager) imm.hideSoftInputFromWindow(this.windowToken, 0)
}

fun View.showKeyboard() {
    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)
}

fun View.doOnClick(clickAction: (view: View) -> Unit) {
    this.setOnClickListener(DebouncingOnClickListener(clickAction))
}

fun View.markAsNotImplemented(): Unit {
    this.setOnClickListener { Toast.makeText(context, "Not implemented yet", Toast.LENGTH_SHORT).show() }
}

fun WebView.setUserAgent() {
    val userAgent = this.settings.userAgentString
    val wvIndex = userAgent.indexOf("wv")
    val sbAgent = StringBuilder(userAgent).insert(wvIndex + 2, "; GetTransfer")
    this.settings.userAgentString = sbAgent.toString()
}

fun View.show(isShow: Boolean, isGone: Boolean = true) {
    visibility = if (isShow)
        View.VISIBLE
    else {
        if (isGone)
            View.GONE
        else
            View.INVISIBLE
    }
}

private var lastClickTimestamp = 0L

fun View.setThrottledClickListener(delay: Long = 400L, clickListener: ((View) -> Unit)?) {
    clickListener?.let { listener ->
        setOnClickListener {
            val currentTimestamp = System.currentTimeMillis()
            val delta = currentTimestamp - lastClickTimestamp
            if (delay <= delta) {
                lastClickTimestamp = currentTimestamp
                listener(this)
            }
        }
    } ?: setOnClickListener(null)
}
