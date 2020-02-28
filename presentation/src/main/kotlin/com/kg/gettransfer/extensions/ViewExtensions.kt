package com.kg.gettransfer.extensions

import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.os.Build
import android.view.View
import android.view.WindowManager.LayoutParams.FLAG_FULLSCREEN
import android.view.inputmethod.InputMethodManager
import android.webkit.WebView

import androidx.annotation.ColorRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment

import com.kg.gettransfer.R

fun Fragment.hideKeyboard() {
    requireActivity().hideKeyboard(requireView())
}

fun AppCompatActivity.hideKeyboard() {
    hideKeyboard(if (currentFocus == null) View(this) else currentFocus)
}

fun Context.hideKeyboard(view: View?) {
    val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.hideSoftInputFromWindow(view?.windowToken, 0)
}

fun View.hideKeyboard() {
    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE)
    if (imm is InputMethodManager) imm.hideSoftInputFromWindow(this.windowToken, 0)
}

fun View.showKeyboard() {
    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)
}

fun WebView.setUserAgent() {
    val userAgent = this.settings.userAgentString
    val wvIndex = userAgent.indexOf("wv")
    val sbAgent = StringBuilder(userAgent).insert(wvIndex + 2, "; GetTransfer")
    this.settings.userAgentString = sbAgent.toString()
}

private var lastClickTimestamp = 0L

fun View.setThrottledClickListener(delay: Long = 500L, clickListener: ((View) -> Unit)?) {
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

fun AppCompatActivity.setStatusBarColor(@ColorRes color: Int) {
    if (color == R.color.colorWhite) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            window.statusBarColor = ContextCompat.getColor(this, color)
        } else {
            window.setFlags(FLAG_FULLSCREEN, FLAG_FULLSCREEN)
        }
    } else {
        window.statusBarColor = ContextCompat.getColor(this, color)
    }
}

fun AppCompatActivity.setTransparentStatusBar() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        window.statusBarColor = Color.TRANSPARENT
    } else {
        window.setFlags(FLAG_FULLSCREEN, FLAG_FULLSCREEN)
    }
}
