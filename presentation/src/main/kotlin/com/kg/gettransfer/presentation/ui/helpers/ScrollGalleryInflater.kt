package com.kg.gettransfer.presentation.ui.helpers

import android.widget.ImageView
import android.widget.LinearLayout
import com.kg.gettransfer.presentation.ui.Utils

object ScrollGalleryInflater {

    fun addImageViews(count: Int, container: LinearLayout) =
        with(container) {
            val firstItemMarginStart = if (count == 1) MARGIN_START_FIRST_SINGLE else MARGIN_START_FIRST
            for (index in 0 until count)
                addView(ImageView(context).apply {
                }, index, LayoutHelper.createLinearParams(
                        Utils.dpToPxInt(context, if (index == 0) firstItemMarginStart else MARGIN_START),
                        MARGIN_TOP,
                        MARGIN_END,
                        MARGIN_BOTTOM,
                        width = Utils.dpToPxInt(context, IMAGE_WIDTH),
                        height = Utils.dpToPxInt(context, IMAGE_HEIGHT)))
        }

    private const val MARGIN_START_FIRST = 0F
    private const val MARGIN_START_FIRST_SINGLE = 16F
    private const val MARGIN_START = 8F
    private const val MARGIN_TOP = 0
    private const val MARGIN_END = 0
    private const val MARGIN_BOTTOM = 0
    private const val IMAGE_WIDTH = 272F
    private const val IMAGE_HEIGHT = 172F
}