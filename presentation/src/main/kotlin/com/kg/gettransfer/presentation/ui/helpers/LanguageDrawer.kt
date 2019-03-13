package com.kg.gettransfer.presentation.ui.helpers

import android.widget.ImageView
import android.widget.LinearLayout
import com.kg.gettransfer.extensions.ceil
import com.kg.gettransfer.presentation.model.LocaleModel
import com.kg.gettransfer.presentation.ui.Utils

object LanguageDrawer {

    fun drawSingleLine(layout: LinearLayout, languages: List<LocaleModel>) {
        layout.removeAllViews()
        val lp = LayoutHelper.createLinearParams(LEFT_MARGIN, TOP_MARGIN, RIGHT_MARGIN, BOTTOM_MARGIN)
        languages.forEach {
            layout.addView(ImageView(layout.context).apply {
                setImageResource(Utils.getLanguageImage(it.delegate.language))
                layoutParams = lp
            })
        }
    }

    fun drawMultipleLine (container: LinearLayout, rowNumber: Int = ITEM_COLUMNS, languages: List<LocaleModel>) {
        container.removeAllViews()
        val lp = LayoutHelper.createLinearParams(LEFT_MARGIN, TOP_MARGIN, RIGHT_MARGIN, BOTTOM_MARGIN)
        lp.width = Utils.dpToPxInt(container.context, 14f)
        lp.height = Utils.dpToPxInt(container.context, 8f)
        for (row in 0 until languages.size.ceil(rowNumber)) {
            val layout = LayoutHelper.createLinear(container.context)
            for (col in 0 until rowNumber) {
                val i = row * rowNumber + col
                if (i == languages.size) break
                layout.addView(ImageView(layout.context).apply {
                    setImageResource(Utils.getLanguageImage(languages[i].delegate.language))
                    layoutParams = lp
                }, col)
            }
            container.addView(layout, row)
        }
    }

    private const val LEFT_MARGIN   = 8
    private const val RIGHT_MARGIN  = 8
    private const val TOP_MARGIN    = 4
    private const val BOTTOM_MARGIN = 4


    private const val ITEM_COLUMNS  = 3
}