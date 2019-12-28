package com.kg.gettransfer.extensions

import android.graphics.Paint
import android.widget.TextView
import androidx.core.view.isVisible

inline var TextView.strikeText: String
    get() = text.toString()
    set(value) {
        paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
        text = value
    }

inline var TextView.visibleText: String
    get() = text.toString()
    set(value) {
        text = value
        this.isVisible = true
}