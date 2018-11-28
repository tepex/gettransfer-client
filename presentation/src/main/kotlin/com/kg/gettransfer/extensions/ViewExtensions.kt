package com.kg.gettransfer.extensions

import android.content.Context

import android.view.View
import android.view.inputmethod.InputMethodManager

import android.widget.Toast

import com.kg.gettransfer.common.DebouncingOnClickListener

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

fun View.hideKeyboard() {
    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(this.windowToken, 0)
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
