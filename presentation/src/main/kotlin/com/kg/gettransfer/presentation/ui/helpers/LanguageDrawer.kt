package com.kg.gettransfer.presentation.ui.helpers

import android.content.Context
import android.widget.ImageView
import android.widget.LinearLayout

import com.kg.gettransfer.R
import com.kg.gettransfer.extensions.ceil
import com.kg.gettransfer.presentation.model.LocaleModel
import com.kg.gettransfer.presentation.ui.Utils

object LanguageDrawer {

    internal const val ITEM_COLUMNS = 3

    fun drawSingleLine(layout: LinearLayout, languages: List<LocaleModel>) {
        layout.removeAllViews()
        languages.forEach {
            layout.addView(ImageView(layout.context).apply {
                setImageResource(Utils.getLanguageImage(it.delegate.toLanguageTag()))
                layoutParams = getLayoutParamsWithMargin(layout.context)
            })
        }
    }

    fun drawMultipleLine (container: LinearLayout, languages: List<LocaleModel>, rowNumber: Int) {
        container.removeAllViews()
        val lp = getLayoutParamsWithMargin(container.context)
        val resources = container.context.resources
        lp.width = resources.getDimensionPixelSize(R.dimen.view_offer_language_icon_width)
        lp.height = resources.getDimensionPixelSize(R.dimen.view_offer_language_icon_height)

        for (row in 0 until languages.size.ceil(rowNumber)) {
            val layout = LayoutHelper.createLinear(container.context)
            for (col in 0 until rowNumber) {
                val i = row * rowNumber + col
                if (i == languages.size) break
                layout.addView(ImageView(layout.context).apply {
                    setImageResource(Utils.getLanguageImage(languages[i].delegate.toLanguageTag()))
                    layoutParams = lp
                }, col)
            }
            container.addView(layout, row)
        }
    }

    private fun getLayoutParamsWithMargin(context: Context): LinearLayout.LayoutParams {
        val resources = context.resources
        val startEndMargin = resources.getDimensionPixelSize(R.dimen.view_offer_language_icon_start_end_margin)
        val topBottomMargin = resources.getDimensionPixelSize(R.dimen.view_offer_language_icon_top_bottom_margin)
        return LayoutHelper.createLinearParams(startEndMargin, topBottomMargin, startEndMargin, topBottomMargin)
    }
}
