package com.kg.gettransfer.extensions

import android.widget.EditText

fun EditText.setUneditable() {
    keyListener     = null
    isCursorVisible = false
    isFocusable     = false
    isClickable     = true
}

fun EditText.getString() =
        this.text.toString()