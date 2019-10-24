package com.kg.gettransfer.presentation.ui.custom

import android.content.Context
import androidx.constraintlayout.widget.ConstraintLayout
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import androidx.core.content.ContextCompat
import com.kg.gettransfer.R
import com.kg.gettransfer.extensions.isGone
import com.kg.gettransfer.extensions.isVisible
import com.kg.gettransfer.presentation.ui.SearchFragment
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.a_b_orange_view.view.*
import kotlinx.android.synthetic.main.a_b_orange_view.view.hourly_point
import kotlinx.android.synthetic.main.a_b_orange_view.view.tv_b_point
import kotlinx.android.synthetic.main.search_address.*
import kotlinx.android.synthetic.main.search_form.*
import kotlinx.android.synthetic.main.search_form.view.icons_container

class SearchFormView @JvmOverloads constructor(
        context: Context,
        attributeSet: AttributeSet? = null,
        defStyle: Int = 0
) : ConstraintLayout(context, attributeSet, defStyle), LayoutContainer {
    override val containerView: View = LayoutInflater.from(context).inflate(R.layout.search_form, this, true)

    fun changeFocus(isToField: Boolean) {
        if (isToField) searchTo.changeFocus() else searchFrom.changeFocus()
    }

    fun markFiledFilled(isToField: Boolean) {
        if (isToField) icons_container.tv_b_point.background =
                ContextCompat.getDrawable(context, R.drawable.back_circle_marker_orange_filled)
                        .also { icons_container.tv_b_point.setTextColor(ContextCompat.getColor(context, R.color.colorWhite)) }
        else icons_container.tv_a_point.background =
                ContextCompat.getDrawable(context, R.drawable.back_circle_marker_orange_filled)
                        .also { icons_container.tv_a_point.setTextColor(ContextCompat.getColor(context, R.color.colorWhite)) }
    }

    fun markFieldEmpty(isToField: Boolean) {
        if (isToField) icons_container.tv_b_point.background =
                ContextCompat.getDrawable(context, R.drawable.back_orange_empty)
                        .also { icons_container.tv_b_point.setTextColor(ContextCompat.getColor(context, R.color.colorTextBlack)) }
        else icons_container.tv_a_point.background =
                ContextCompat.getDrawable(context, R.drawable.back_orange_empty)
                        .also { icons_container.tv_a_point.setTextColor(ContextCompat.getColor(context, R.color.colorTextBlack)) }
    }

    fun initFromWidget(parent: SearchFragment, title: String) {
        searchFrom.initWidget(parent, false)
        searchFrom.sub_title.text = title
    }

    fun initToWidget(parent: SearchFragment, title: String) {
        searchTo.initWidget(parent, true)
        searchTo.sub_title.text = title
    }

    fun changeViewToHourlyDuration() {
        searchTo.isGone = true
        tv_b_point.isVisible = false
        hourly_point.isVisible = true
        rl_hourly.isVisible = true
    }

    fun findPopularPlace(isToField: Boolean, place: String) {
        val searchField = if (isToField) searchTo else searchFrom
        searchField.initText(place, true, true)
    }
}