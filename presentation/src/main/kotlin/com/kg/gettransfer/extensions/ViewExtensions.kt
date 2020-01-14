package com.kg.gettransfer.extensions

import android.app.Activity
import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.webkit.WebView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment

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
