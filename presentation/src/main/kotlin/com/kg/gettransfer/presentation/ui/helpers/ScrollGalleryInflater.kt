package com.kg.gettransfer.presentation.ui.helpers

import android.content.Context
import android.widget.ImageView
import android.widget.LinearLayout
import com.kg.gettransfer.presentation.ui.Utils

object ScrollGalleryInflater {

    fun addImageViews(count: Int, container: LinearLayout) =
        with(container) {
            for (index in 0 until count)
                addView(ImageView(context), index, LayoutHelper.createLinearParams(
                        Utils.dpToPxInt(context, if (index == 0) MARGIN_START_FIRST else MARGIN_START),
                        MARGIN_TOP,
                        MARGIN_END,
                        MARGIN_BOTTOM))
        }

    private const val MARGIN_START_FIRST = 0F
    private const val MARGIN_START = 8F
    private const val MARGIN_TOP = 0
    private const val MARGIN_END = 0
    private const val MARGIN_BOTTOM = 0
}