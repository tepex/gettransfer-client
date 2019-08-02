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

    fun createLinearParams(width: Int, height: Int, l: Int, t: Int, r: Int, b: Int) =
        LinearLayout.LayoutParams (width, height)
            .apply { setMargins(l, t, r, b) }

    fun createLinearParams(width: Int, height: Int, vertical: Int, horizontal: Int) =
        LinearLayout.LayoutParams (width, height)
            .apply { setMargins(horizontal, vertical, horizontal, vertical) }
}