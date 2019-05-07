package com.kg.gettransfer.presentation.ui.helpers

import android.content.Context
import android.view.Gravity
import android.widget.LinearLayout

object LayoutHelper {

    fun createLinear(context: Context, horizontal: Boolean = true) =
            LinearLayout(context)
                    .apply {
                        orientation = if (horizontal) LinearLayout.HORIZONTAL else LinearLayout.VERTICAL
                        gravity = Gravity.END
                    }

    fun createLinearParams(l: Int, t: Int, r: Int, b: Int, width: Int? = null, height: Int? = null) =
            LinearLayout.LayoutParams (
                    width ?: LinearLayout.LayoutParams.MATCH_PARENT,
                    height ?: LinearLayout.LayoutParams.MATCH_PARENT)
                    .apply { setMargins(l, t, r, b) }
}