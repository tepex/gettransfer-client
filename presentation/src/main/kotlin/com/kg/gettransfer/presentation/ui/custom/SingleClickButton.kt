package com.kg.gettransfer.presentation.ui.custom

import android.content.Context
import android.util.AttributeSet

import androidx.appcompat.widget.AppCompatImageButton

class SingleClickButton(context: Context, attrs: AttributeSet) : AppCompatImageButton(context, attrs) {

    override fun setOnClickListener(l: OnClickListener?) {
        super.setOnClickListener { view ->
            isEnabled = false
            l?.onClick(view)
        }
    }
}
