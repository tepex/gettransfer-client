package com.kg.gettransfer.presentation.ui.custom

import android.content.Context
import android.util.AttributeSet

class SingleClickButton : android.support.v7.widget.AppCompatImageButton {

    constructor(context: Context) : super(context) {}

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {}

    override fun setOnClickListener(l: OnClickListener?) {
        super.setOnClickListener {
            isEnabled = false

            l?.onClick(it)
        }
    }
}