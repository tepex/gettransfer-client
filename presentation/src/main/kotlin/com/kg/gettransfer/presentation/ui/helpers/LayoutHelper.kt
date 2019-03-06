package com.kg.gettransfer.presentation.ui.helpers

import android.content.Context
import android.view.Gravity
import android.widget.LinearLayout

object LayoutHelper {

    fun createLinear(context: Context, horizontal: Boolean = true) =
            LinearLayout(context)
                    .apply {
                        orientation = if (horizontal) LinearLayout.HORIZONTAL else LinearLayout.VERTICAL
                        gravity = Gravity.START
                    }

    fun createLinearParams(l: Int, t: Int, r: Int, b: Int) =
            LinearLayout.LayoutParams (
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT)
                    .apply {
                        setMargins(l, t, r, b)
                    }


}