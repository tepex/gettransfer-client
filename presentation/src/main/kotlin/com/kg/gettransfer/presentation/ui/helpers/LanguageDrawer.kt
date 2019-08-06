package com.kg.gettransfer.presentation.ui.helpers

import android.content.res.Resources
import android.widget.ImageView
import android.widget.LinearLayout

import com.kg.gettransfer.R
import com.kg.gettransfer.extensions.ceil
import com.kg.gettransfer.presentation.model.LocaleModel
import com.kg.gettransfer.presentation.ui.Utils

object LanguageDrawer {

    internal const val DEFAULT_ITEM_COLUMNS = 3

    enum class LanguageLayoutParamsRes(
        val width: Int,
        val height: Int,
        val verticalMargins: Int,
        val horizontalMargins: Int
    ) {
        OFFER_ITEM(
            R.dimen.view_offer_language_icon_width,
            R.dimen.view_offer_language_icon_height,
            R.dimen.view_offer_language_icon_vertical_margins,
            R.dimen.view_offer_language_icon_horizontal_margins
        ),
        OFFER_DETAILS(
            R.dimen.bottom_sheet_offer_details_language_icon_width,
            R.dimen.bottom_sheet_offer_details_language_icon_height,
            R.dimen.bottom_sheet_offer_details_language_icon_vertical_margins,
            R.dimen.bottom_sheet_offer_details_language_icon_horizontal_margins
        ),
        OFFER_PAYMENT_VIEW(
            R.dimen.payment_offer_tiny_language_width,
            R.dimen.payment_offer_tiny_language_height,
            R.dimen.payment_offer_tiny_language_vertical_margins,
            R.dimen.payment_offer_tiny_language_horizontal_margins
        ),
        TRANSFER_DETAILS(
            R.dimen.view_transfer_details_language_width,
            R.dimen.view_transfer_details_language_height,
            R.dimen.view_transfer_details_language_vertical_margins,
            R.dimen.view_transfer_details_language_horizontal_margins
        ),
        SETTINGS(
            R.dimen.activity_settings_language_icon_width,
            R.dimen.activity_settings_language_icon_height,
            R.dimen.activity_settings_language_icon_margins,
            R.dimen.activity_settings_language_icon_margins
        )
    }

    fun drawSingleLine(container: LinearLayout, languages: List<LocaleModel>, lpRes: LanguageLayoutParamsRes) {
        container.removeAllViews()
        val lp = getLayoutParamsWithMargin(container.context.resources, lpRes)

        languages.forEach { lang ->
            container.addView(ImageView(container.context).apply {
                setImageResource(Utils.getLanguageImage(lang.delegate.toLanguageTag()))
                layoutParams = lp
            })
        }
    }

    fun drawMultipleLine(
        container: LinearLayout,
        languages: List<LocaleModel>,
        colNumber: Int,
        lpRes: LanguageLayoutParamsRes
    ) {
        container.removeAllViews()
        val lp = getLayoutParamsWithMargin(container.context.resources, lpRes)

        for (row in 0 until languages.size.ceil(colNumber)) {
            val lineLayout = LayoutHelper.createLinear(container.context)
            for (col in 0 until colNumber) {
                val i = row * colNumber + col
                if (i == languages.size) break
                lineLayout.addView(ImageView(lineLayout.context).apply {
                    setImageResource(Utils.getLanguageImage(languages[i].delegate.toLanguageTag()))
                    layoutParams = lp
                }, col)
            }
            container.addView(lineLayout, row)
        }
    }

    private fun getLayoutParamsWithMargin(resources: Resources, lpRes: LanguageLayoutParamsRes) =
        LayoutHelper.createLinearParams(
            resources.getDimensionPixelSize(lpRes.width),
            resources.getDimensionPixelSize(lpRes.height),
            resources.getDimensionPixelSize(lpRes.verticalMargins),
            resources.getDimensionPixelSize(lpRes.horizontalMargins)
        )
}
