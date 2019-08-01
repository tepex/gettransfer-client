package com.kg.gettransfer.presentation.ui.custom

import android.content.Context
import android.util.AttributeSet

class SingleClickButton @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet
) : android.support.v7.widget.AppCompatImageButton(context, attrs) {

    override fun setOnClickListener(l: OnClickListener?) {
        super.setOnClickListener { view ->
            isEnabled = false
            l?.onClick(view)
        }
    }
}
