package com.kg.gettransfer.views

import android.text.Editable
import android.text.TextWatcher
import android.view.MotionEvent
import android.view.View
import android.widget.EditText
import com.kg.gettransfer.R

/**
 * Created by denisvakulenko on 09/02/2018.
 */

fun EditText.setupClearButtonWithAction() {
    addTextChangedListener(object : TextWatcher {
        override fun afterTextChanged(editable: Editable?) {
            val clearIcon = if (editable?.isNotEmpty() == true) R.drawable.ic_clear_gray_24dp else 0
            setCompoundDrawablesWithIntrinsicBounds(0, 0, clearIcon, 0)
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) = Unit
    })

    setOnTouchListener(View.OnTouchListener { _, event ->
        if (event.action == MotionEvent.ACTION_UP) {
            if (event.rawX >= (this.right - this.compoundPaddingRight)) {
                this.setText("")
                return@OnTouchListener true
            }
        }
        return@OnTouchListener false
    })
}

fun EditText.setupChooseDate() {
//    addTextChangedListener(object : TextWatcher {
//        override fun afterTextChanged(editable: Editable?) {
    val clearIcon = R.drawable.ic_calendar_black_24dp
    setCompoundDrawablesWithIntrinsicBounds(0, 0, clearIcon, 0)
//        }
//
//        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit
//        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) = Unit
//    })
//
//    setOnTouchListener(View.OnTouchListener { _, event ->
//        if (event.action == MotionEvent.ACTION_UP) {
//            if (event.rawX >= (this.right - this.compoundPaddingRight)) {
//
////                return@OnTouchListener true
//            }
//        }
//        return@OnTouchListener false
//    })
}

fun EditText.setupChooseTime() {
    val clearIcon = R.drawable.ic_access_time_black_24dp
    setCompoundDrawablesWithIntrinsicBounds(0, 0, clearIcon, 0)
//
//
//    addTextChangedListener(object : TextWatcher {
//        override fun afterTextChanged(editable: Editable?) {
//            val clearIcon = R.drawable.ic_access_time_black_24dp
//            setCompoundDrawablesWithIntrinsicBounds(0, 0, clearIcon, 0)
//        }
//
//        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit
//        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) = Unit
//    })
//
//    setOnTouchListener(View.OnTouchListener { _, event ->
//        if (event.action == MotionEvent.ACTION_UP) {
//            if (event.rawX >= (this.right - this.compoundPaddingRight)) {
//
////                return@OnTouchListener true
//            }
//        }
//        return@OnTouchListener false
//    })
}